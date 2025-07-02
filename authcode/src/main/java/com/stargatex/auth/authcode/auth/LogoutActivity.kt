package com.stargatex.auth.authcode.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.stargatex.auth.authcode.extensions.safeGetParcelable
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import com.stargatex.auth.authcode.model.flow.EndSessionFlowResults
import com.stargatex.auth.authcode.model.logout.LogoutRequest
import com.stargatex.auth.authcode.model.logout.LogoutResponse
import com.stargatex.auth.authcode.util.AuthBundleArgKey
import net.openid.appauth.AuthorizationException
import net.openid.appauth.EndSessionResponse

internal class LogoutActivity : Activity() {

    private lateinit var endSessionFlowResults: EndSessionFlowResults
    private var completeIntent: PendingIntent? = null
    private var failedIntent: PendingIntent? = null

    companion object {
        private const val COMPLETE_INTENT = "COMPLETE_INTENT"
        private const val FAILED_INTENT = "FAILED_INTENT"

        fun getPendingIntent(
            context: Context,
            completeIntent: PendingIntent,
            failedIntent: PendingIntent
        ): PendingIntent {
            val intent = Intent(context, LogoutActivity::class.java).apply {
                putExtra(COMPLETE_INTENT, completeIntent)
                putExtra(FAILED_INTENT, failedIntent)
            }
            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT

            return PendingIntent.getActivity(context, 0, intent, flags)
        }

        @JvmStatic
        fun getLogoutResultFromIntent(intent: Intent): EndSessionFlowResults? {
            return intent.extras?.safeGetParcelable(AuthBundleArgKey.END_SESSION_FLOW_RESULTS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            state(intent.extras)
        } else state(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        endSessionFlowResults = EndSessionFlowResults()

        val endSessionResponse = EndSessionResponse.fromIntent(intent)


        val authorizationException = AuthorizationException.fromIntent(intent)
        if (endSessionResponse == null) {
            endSessionFlowResults.authError =
                AuthFlowExceptionHandler.toCustomAuthException(authorizationException)
            sendIntent(failedIntent)
            return
        }

        endSessionFlowResults.logoutResponse = LogoutResponse(
            logoutRequest = LogoutRequest(
                mIdTokenHint = endSessionResponse.request.idTokenHint,
                mPostLogoutRedirectUri = endSessionResponse.request.postLogoutRedirectUri,
                mState = endSessionResponse.request.state,
                mUiLocales = endSessionResponse.request.uiLocales
            ),
            state = endSessionResponse.state
        )
        sendIntent(completeIntent)
    }

    private fun sendIntent(completeIntent: PendingIntent?) {
        val resultIntent = Intent().apply {
            putExtra(AuthBundleArgKey.END_SESSION_FLOW_RESULTS, endSessionFlowResults)
        }

        try {
            completeIntent?.send(this, 0, resultIntent)
        } catch (ex: PendingIntent.CanceledException) {
            Log.e(LogoutActivity::class.simpleName, "Error sending PendingIntent", ex)
        } finally {
            finish()
        }
    }

    private fun state(extras: Bundle?) {

        with(extras) {
            if (this == null) {
                Log.d(
                    LogoutActivity::class.simpleName,
                    "Empty extras"
                )
                finish()
            } else {
                completeIntent = this.safeGetParcelable(COMPLETE_INTENT)
                failedIntent = this.safeGetParcelable(FAILED_INTENT)
            }
        }

    }

}