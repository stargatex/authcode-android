package com.stargatex.auth.authcode.auth

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.stargatex.auth.authcode.configs.auth.DefaultAuthConfiguration
import com.stargatex.auth.authcode.configs.client.ClientSecretConfig
import com.stargatex.auth.authcode.model.common.ResourceResult
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.authcode.model.token.TokenData
import com.stargatex.auth.authcode.util.AuthBundleArgKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

internal class AuthActivity : Activity() {

    private lateinit var authManager: AuthManger
    private var completeIntent: PendingIntent? = null
    private var failedIntent: PendingIntent? = null
    private lateinit var authorizationCodeFlowResults: AuthorizationCodeFlowResults

    internal companion object {
        fun getPendingIntent(
            context: Context,
            completeIntent: PendingIntent,
            failedIntent: PendingIntent
        ): PendingIntent {
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(COMPLETE_INTENT, completeIntent)
            intent.putExtra(FAILED_INTENT, failedIntent)
            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else FLAG_UPDATE_CURRENT
            return PendingIntent.getActivity(context, 0, intent, flags)
        }

      internal fun getPendingIntent(
            context: Context,
            completeIntent: PendingIntent,
            failedIntent: PendingIntent,
            clientSecretConfig: ClientSecretConfig?
        ): PendingIntent {
          val intent = Intent(context, AuthActivity::class.java)
          intent.putExtra(COMPLETE_INTENT, completeIntent)
          intent.putExtra(FAILED_INTENT, failedIntent)
          this.clientSecretConfig = clientSecretConfig

          val flags =
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else FLAG_UPDATE_CURRENT
          return PendingIntent.getActivity(context, 0, intent, flags)
      }


        @JvmStatic
        fun getLoginResultFromIntent(intent: Intent): AuthorizationCodeFlowResults? {
            return when (intent.extras?.containsKey(AuthBundleArgKey.AUTH_FLOW_RESULTS)) {
                true -> intent.extras?.getParcelable(AuthBundleArgKey.AUTH_FLOW_RESULTS)
                else -> null
            }
        }

        private var clientSecretConfig: ClientSecretConfig? = null
        const val COMPLETE_INTENT = "COMPLETE_INTENT"
        const val FAILED_INTENT = "FAILED_INTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            state(intent.extras)
        } else state(savedInstanceState)

    }

    private fun state(extras: Bundle?) {

        with(extras) {
            if (this == null) {
                Log.d(
                    AuthActivity::class.simpleName,
                    "Empty extras"
                )
                finish()
            } else {
                completeIntent = this.getParcelable(COMPLETE_INTENT)
                failedIntent = this.getParcelable(FAILED_INTENT)
                //clientSecretConfig = this.getParcelable(AUTH_FLOW_WITH_CLIENT_AUTH)
            }
        }

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
        exchangeTokenForCode(authorizationResponse)
    }


    private fun exchangeTokenForCode(successIntent: AuthorizationResponse) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenResponse = authManager.exchangeTokenWithCode(successIntent)

                if (tokenResponse != null) {
                    val tokenData = TokenData(
                        accessToken = tokenResponse.accessToken,
                        accessTokenExpirationTime = tokenResponse.accessTokenExpirationTime,
                        idToken = tokenResponse.idToken,
                        refreshToken = tokenResponse.refreshToken,
                        scope = tokenResponse.tokenType,
                        tokenType = tokenResponse.tokenType
                    )
                    val success = ResourceResult.Success(tokenData)
                    authorizationCodeFlowResults.tokenResult = tokenData
                    withContext(Dispatchers.Main) {
                        authManager.dispose()
                        sendCompleteIntent()
                    }
                }
                //throw AuthorizationException()
            } catch (ex: AuthException) {
                Log.e(
                    AuthActivity::class.simpleName,
                    "Token and Code exchange error ", ex
                )
                withContext(Dispatchers.Main) {
                    authorizationCodeFlowResults.authError = ex
                    authManager.dispose()
                    sendFailedIntent()
                }
            }

        }


    }


    private fun sendCompleteIntent() {
        val intent = Intent(this, completeIntent?.intentSender?.javaClass)
        intent.putExtra(AuthBundleArgKey.AUTH_FLOW_RESULTS, authorizationCodeFlowResults)

        try {
            completeIntent?.send(this, 0, intent)
        } catch (ex: PendingIntent.CanceledException) {
            Log.e(AuthActivity::class.simpleName, "Token and Code exchange error ${ex.message}")
        }
        finish()
    }

    private fun sendFailedIntent() {
        val intent = Intent(this, failedIntent?.intentSender?.javaClass)
        intent.putExtra(AuthBundleArgKey.AUTH_FLOW_RESULTS, authorizationCodeFlowResults)

        try {
            failedIntent?.send(this, 0, intent)
        } catch (ex: PendingIntent.CanceledException) {
            Log.e(AuthActivity::class.simpleName, "Token and Code exchange error  ${ex.message}")
        }
        finish()
    }
}