package com.stargatex.auth.authcode.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.stargatex.auth.authcode.configs.auth.DefaultAuthConfiguration
import com.stargatex.auth.authcode.configs.client.ClientSecretConfig
import com.stargatex.auth.authcode.extensions.safeGetParcelable
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.authcode.model.token.TokenData
import com.stargatex.auth.authcode.util.AuthBundleArgKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

internal class AuthActivity : ComponentActivity() {

    private lateinit var authManager: AuthManger
    private var completeIntent: PendingIntent? = null
    private var failedIntent: PendingIntent? = null
    private lateinit var authorizationCodeFlowResults: AuthorizationCodeFlowResults
    private var clientSecretConfig: ClientSecretConfig? = null

    internal companion object {


        private const val COMPLETE_INTENT = "COMPLETE_INTENT"
        private const val FAILED_INTENT = "FAILED_INTENT"
        private const val CLIENT_SECRET_CONFIG = "CLIENT_SECRET_CONFIG"

        fun getPendingIntent(
            context: Context,
            completeIntent: PendingIntent,
            failedIntent: PendingIntent,
            clientSecretConfig: ClientSecretConfig?
        ): PendingIntent {
            val intent = Intent(context, AuthActivity::class.java).apply {
                putExtra(COMPLETE_INTENT, completeIntent)
                putExtra(FAILED_INTENT, failedIntent)
                putExtra(CLIENT_SECRET_CONFIG, clientSecretConfig)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

            return PendingIntent.getActivity(context, 0, intent, flags)
        }

        @JvmStatic
        fun getLoginResultFromIntent(intent: Intent): AuthorizationCodeFlowResults? {
            return when (intent.extras?.containsKey(AuthBundleArgKey.AUTH_FLOW_RESULTS)) {
                true -> intent.extras?.safeGetParcelable(AuthBundleArgKey.AUTH_FLOW_RESULTS)
                else -> null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            state(intent.extras)
        } else state(savedInstanceState)

    }

    private fun state(extras: Bundle?) {

        if (extras == null) {
            Log.e(AuthActivity::class.simpleName, "Missing required intent extras.")
            finish()
            return
        }

        completeIntent = extras.safeGetParcelable(COMPLETE_INTENT)
        failedIntent = extras.safeGetParcelable(FAILED_INTENT)
        clientSecretConfig = extras.safeGetParcelable(CLIENT_SECRET_CONFIG)

    }


    override fun onStart() {
        super.onStart()

        authManager = AuthManger(this, DefaultAuthConfiguration(context = this, clientSecretConfig))
        authorizationCodeFlowResults = AuthorizationCodeFlowResults()

        val authorizationResponse = AuthorizationResponse.fromIntent(intent)
        val authorizationException = AuthorizationException.fromIntent(intent)

        if (authorizationResponse == null) {
            authorizationCodeFlowResults.authError =
                AuthFlowExceptionHandler.toCustomAuthException(authorizationException)
            sendFailedIntent()
            return
        }

        lifecycleScope.launch {
            exchangeTokenForCode(authorizationResponse)
        }

    }


    private suspend fun exchangeTokenForCode(successIntent: AuthorizationResponse) {

            try {
                val tokenResponse = withContext(Dispatchers.IO) {
                    authManager.exchangeTokenWithCode(successIntent)
                }

                if (tokenResponse != null) {
                    val tokenData = TokenData(
                        accessToken = tokenResponse.accessToken,
                        accessTokenExpirationTime = tokenResponse.accessTokenExpirationTime,
                        idToken = tokenResponse.idToken,
                        refreshToken = tokenResponse.refreshToken,
                        scope = tokenResponse.scope,
                        tokenType = tokenResponse.tokenType
                    )
                    authorizationCodeFlowResults.tokenResult = tokenData
                    sendCompleteIntent()
                }
                //throw AuthorizationException()
            } catch (ex: AuthException) {
                Log.e(
                    AuthActivity::class.simpleName,
                    "Token and Code exchange error ", ex
                )
                authorizationCodeFlowResults.authError = ex
                sendFailedIntent()
            }
    }


    private fun sendCompleteIntent() {
        try {
            completeIntent?.send(
                this, 0,
                Intent().putExtra(AuthBundleArgKey.AUTH_FLOW_RESULTS, authorizationCodeFlowResults)
            )
        } catch (ex: PendingIntent.CanceledException) {
            Log.e(AuthActivity::class.simpleName, "Failed to send complete intent", ex)
        }
        finish()
    }

    private fun sendFailedIntent() {
        try {
            failedIntent?.send(
                this, 0,
                Intent().putExtra(AuthBundleArgKey.AUTH_FLOW_RESULTS, authorizationCodeFlowResults)
            )
        } catch (ex: PendingIntent.CanceledException) {
            Log.e(AuthActivity::class.simpleName, "Failed to send failed intent", ex)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::authManager.isInitialized) {
            authManager.dispose()
        }
    }
}