package com.stargatex.auth.authcode.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stargatex.auth.authcode.configs.auth.AuthConfiguration
import com.stargatex.auth.authcode.configs.service.AuthServiceConfiguration
import com.stargatex.auth.authcode.configs.service.CustomAuthServiceConfiguration
import com.stargatex.auth.authcode.extensions.convertToUri
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import net.openid.appauth.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal class AuthManger(var context: Context, private var authConfiguration: AuthConfiguration) {
    private var authorizationService: AuthorizationService = AuthorizationService(context)
    private var authServiceConfiguration: AuthServiceConfiguration =
        CustomAuthServiceConfiguration(context = context, authConfiguration = authConfiguration)


    internal suspend fun getAuthorizationServiceConfiguration(): AuthorizationServiceConfiguration =
        authServiceConfiguration.getAuthorizationServiceConfiguration()

    private fun getAuthorizationRequestIntent(authServiceConfiguration: AuthorizationServiceConfiguration): Intent {
        val authorizationRequest = getAuthorizationRequest(authServiceConfiguration)

        return authorizationService.getAuthorizationRequestIntent(authorizationRequest)
    }

    internal fun performAuthorizationRequest(
        authServiceConfiguration: AuthorizationServiceConfiguration,
        onSuccessIntent: Intent,
        onFailIntent: Intent
    ) {
        val authorizationRequest = getAuthorizationRequest(authServiceConfiguration)
        onSuccessIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val failedPendingIntent = PendingIntent.getActivity(context, 0, onFailIntent, 0)
        val successPendingIntent = PendingIntent.getActivity(context, 0, onSuccessIntent, 0)

        Log.i(AuthManger::class.java.simpleName, "Performing authorization request")
        authorizationService.performAuthorizationRequest(
            authorizationRequest,
            AuthActivity.getPendingIntent(
                context,
                successPendingIntent,
                failedPendingIntent,
                authConfiguration.clientSecretConfig
            ),
            failedPendingIntent
        )
    }

    private fun getAuthorizationRequest(authServiceConfiguration: AuthorizationServiceConfiguration): AuthorizationRequest {


        return AuthorizationRequest.Builder(
            authServiceConfiguration,
            authConfiguration.getOidcConfig().clientId,
            ResponseTypeValues.CODE,
            authConfiguration.getOidcConfig().redirectUri.convertToUri()
        ).setScope(authConfiguration.getOidcConfig().scope)
            // .setAdditionalParameters(parameters)
            .build()
    }


    internal suspend fun exchangeTokenWithCode(successIntent: AuthorizationResponse): TokenResponse? {
        return suspendCoroutine { continuation ->

            Log.i(
                AuthManger::class.java.simpleName, "Token and code exchange started "
            )

            val tokenCallback = { response: TokenResponse?, ex: AuthorizationException? ->
                when {
                    response != null -> {
                        AuthStateManager.saveToken(tokenResponse = response)
                        continuation.resume(response)
                    }
                    else -> {
                        if (ex != null) {
                            continuation.resumeWithException(
                                AuthFlowExceptionHandler.toCustomAuthException(
                                    ex
                                )
                            )
                        }
                    }
                }

            }

            val clientAuthentication = authConfiguration.clientAuthConfig?.clientAuthentication()
            if (clientAuthentication != null) {
                authorizationService.performTokenRequest(
                    successIntent.createTokenExchangeRequest(),
                    clientAuthentication,
                    tokenCallback
                )
            } else authorizationService.performTokenRequest(
                successIntent.createTokenExchangeRequest(),
                tokenCallback
            )
        }
    }

    internal suspend fun refreshAccessToken(
        refreshToken: String,
        authServiceConfiguration: AuthorizationServiceConfiguration
    ): TokenResponse? {
        return suspendCoroutine { continuation ->

            val tokenCallback = { response: TokenResponse?, ex: AuthorizationException? ->
                when {
                    response != null -> {
                        AuthStateManager.saveToken(tokenResponse = response)
                        continuation.resume(response)
                    }
                    else -> {
                        if (ex != null) {
                            continuation.resumeWithException(
                                AuthFlowExceptionHandler.toCustomAuthException(
                                    ex
                                )
                            )
                        }
                    }
                }

            }

            val tokenRequest = TokenRequest.Builder(
                authServiceConfiguration,
                authConfiguration.getOidcConfig().clientId
            )
                .setGrantType(GrantTypeValues.REFRESH_TOKEN)
                .setRefreshToken(refreshToken)
                .build()

            val clientAuthentication = authConfiguration.clientAuthConfig?.clientAuthentication()
            if (clientAuthentication != null) {
                authorizationService.performTokenRequest(
                    tokenRequest,
                    clientAuthentication,
                    tokenCallback
                )
            } else authorizationService.performTokenRequest(tokenRequest, tokenCallback)
        }
    }

    internal fun performEndSessionRequest(
        idToken: String?,
        authorizationServiceConfiguration: AuthorizationServiceConfiguration,
        onCompleteIntent: Intent,
    ): Exception? {
        val endSessionRequest = endSessionRequest(authorizationServiceConfiguration, idToken)

        if (endSessionRequest != null) {
            val failedPendingIntent = PendingIntent.getActivity(context, 0, onCompleteIntent, 0)
            val successPendingIntent = PendingIntent.getActivity(context, 0, onCompleteIntent, 0)
            authorizationService.performEndSessionRequest(
                endSessionRequest,
                LogoutActivity.getPendingIntent(context, successPendingIntent, failedPendingIntent)
            )
            return null
        }
        throw AuthException.ClientException(
            AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
            "End session config error",
            null
        )
    }

    internal fun getEndSessionIntent(
        idToken: String?,
        authorizationServiceConfiguration: AuthorizationServiceConfiguration
    ): Intent? {
        val endSessionRequest = endSessionRequest(authorizationServiceConfiguration, idToken)
        return endSessionRequest?.let { authorizationService.getEndSessionRequestIntent(it) }
    }

    private fun endSessionRequest(
        authorizationServiceConfiguration: AuthorizationServiceConfiguration,
        idToken: String?
    ): EndSessionRequest? {
        return authorizationServiceConfiguration.endSessionEndpoint?.let {
            EndSessionRequest.Builder(authorizationServiceConfiguration)
                .setIdTokenHint(idToken)
                .setPostLogoutRedirectUri(authConfiguration.getOidcConfig().logoutRedirectUri.convertToUri())
                .build()
        }
    }

    internal fun dispose() {
        authorizationService.dispose()
    }

    internal fun disposeAndClear() {
        authorizationService.dispose()
        AuthStateManager.clear()
    }


}