package com.stargatex.auth.authcode.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.stargatex.auth.authcode.configs.auth.AuthConfiguration
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.authcode.model.token.TokenData
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public class DefaultWebAuthProvider internal constructor(private var context: Context) : WebAuthProvider {
    private lateinit var authConfiguration: AuthConfiguration
    private lateinit var authServiceConfiguration: AuthorizationServiceConfiguration
    private lateinit var authorizationService: AuthorizationService
    private lateinit var authManger: AuthManger
    private val mutex = Mutex()

    public constructor(context: Context, authConfiguration: AuthConfiguration) : this(context) {
        this.authConfiguration = authConfiguration
        this.authorizationService = AuthorizationService(context)
        this.authManger = AuthManger(context, authConfiguration)
    }

    override fun login(context: Context, onSuccessIntent: Intent, onFailIntent: Intent) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                authServiceConfiguration = authManger.getAuthorizationServiceConfiguration()

                withContext(Dispatchers.Main) {
                    authManger.performAuthorizationRequest(
                        authServiceConfiguration,
                        onSuccessIntent,
                        onFailIntent
                    )
                }

            } catch (ex: AuthException) {
                Log.e(
                    DefaultWebAuthProvider::class.simpleName,
                    "${ex.message} : ${ex.description}",
                    ex
                )
            } catch (ex: Exception) {
                Log.e(DefaultWebAuthProvider::class.simpleName, ex.message, ex)
            }
        }
    }


    override fun logout(context: Context, idToken: String, onCompleteIntent: Intent) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                authServiceConfiguration = authManger.getAuthorizationServiceConfiguration()
                withContext(Dispatchers.Main) {
                    authManger.performEndSessionRequest(
                        idToken,
                        authServiceConfiguration,
                        onCompleteIntent
                    )
                }

            } catch (ex: AuthException) {
                Log.e(
                    DefaultWebAuthProvider::class.simpleName,
                    "${ex.message} : ${ex.description}",
                    ex
                )
            } catch (ex: Exception) {
                Log.e(DefaultWebAuthProvider::class.simpleName, ex.message, ex)
            }
        }
    }

    override fun refreshAccessToken(
        context: Context,
        refreshToken: String
    ): AuthorizationCodeFlowResults = runBlocking(Dispatchers.IO) {
        mutex.withLock {
            val tokenResponse: TokenResponse?
            val tokenData: TokenData
            val authorizationCodeFlowResults = AuthorizationCodeFlowResults()
            try {
                authServiceConfiguration = authManger.getAuthorizationServiceConfiguration()


                tokenResponse =
                    authManger.refreshAccessToken(refreshToken, authServiceConfiguration)


                if (tokenResponse != null) {

                    tokenData = TokenData(
                        accessToken = tokenResponse.accessToken,
                        accessTokenExpirationTime = tokenResponse.accessTokenExpirationTime,
                        idToken = tokenResponse.idToken,
                        refreshToken = tokenResponse.refreshToken,
                        scope = tokenResponse.tokenType,
                        tokenType = tokenResponse.tokenType
                    )
                    Log.i(
                        DefaultWebAuthProvider::class.simpleName,
                        "Token refreshed"
                    )
                    authorizationCodeFlowResults.tokenResult = tokenData
                    return@runBlocking authorizationCodeFlowResults
                }


            } catch (ex: AuthException) {
                Log.e(
                    DefaultWebAuthProvider::class.simpleName,
                    "${ex.message} ::: ${ex.description}",
                    ex
                )
                authorizationCodeFlowResults.authError = ex

            } catch (ex: Exception) {
                Log.e(DefaultWebAuthProvider::class.simpleName, ex.message, ex)
                authorizationCodeFlowResults.authError = AuthException.ClientException(
                    AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                    "Cannot refresh token error",
                    ex.localizedMessage
                )
            }
            return@runBlocking authorizationCodeFlowResults
        }
    }

}