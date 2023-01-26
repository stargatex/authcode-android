package com.stargatex.auth.strgtxauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

import com.stargatex.auth.authcode.auth.DefaultWebAuthProvider
import com.stargatex.auth.authcode.auth.WebAuthProvider
import com.stargatex.auth.authcode.configs.auth.DefaultAuthConfiguration
import com.stargatex.auth.authcode.configs.client.ClientPostSecretConfig
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowResultHandler
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.authcode.model.flow.EndSessionFlowResults
import com.stargatex.auth.strgtxauth.util.AuthConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserActivity : AppCompatActivity() {
    private var endSessionFlowResults: EndSessionFlowResults? = null

    private var authorizationCodeFlowResults: AuthorizationCodeFlowResults? = null
    lateinit var webAuthProvider: WebAuthProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val logout = findViewById<MaterialButton>(R.id.logout)
        val refresh = findViewById<MaterialButton>(R.id.refresh)




        webAuthProvider = DefaultWebAuthProvider(
            this, DefaultAuthConfiguration(
                context = this, ClientPostSecretConfig(
                    AuthConstant.CLIENT_SECRET
                )
            )
        )


        logout.setOnClickListener(View.OnClickListener {
            if (authorizationCodeFlowResults?.tokenResult?.idToken == null) return@OnClickListener
            webAuthProvider.logout(
                this, authorizationCodeFlowResults?.tokenResult?.idToken!!,
                Intent(this, UserActivity::class.java)
            )
        })

        refresh.setOnClickListener(View.OnClickListener {
            Log.d(
                UserActivity::class.simpleName,
                "is Token res ${authorizationCodeFlowResults?.tokenResult?.refreshToken}"
            )
            if (authorizationCodeFlowResults?.tokenResult?.refreshToken == null) return@OnClickListener

            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val authorizationCodeFlowResultsNew =
                        (webAuthProvider as DefaultWebAuthProvider).refreshAccessToken(
                            applicationContext,
                            authorizationCodeFlowResults?.tokenResult?.refreshToken!!
                        )

                    Log.e(
                        UserActivity::class.java.simpleName,
                        "authorizationCodeFlowResultsNew error ${authorizationCodeFlowResultsNew.authError}  "
                    )

                    Log.d(
                        UserActivity::class.java.simpleName,
                        "authorizationCodeFlowResultsNew token ${authorizationCodeFlowResultsNew.tokenResult}  "
                    )
                }
            } catch (ex: AuthException) {
                Log.e(
                    UserActivity::class.java.simpleName,
                    "refresh.setOnClickListener  $ex} "
                )
            }

        })
    }

    override fun onResume() {
        super.onResume()

        authorizationCodeFlowResults = AuthFlowResultHandler.getLoginResultFromIntent(intent)

        Log.d(
            UserActivity::class.simpleName,
            "authorizationCodeFlowResults ${Gson().toJson(authorizationCodeFlowResults?.tokenResult)}"
        )
        Log.d(
            UserActivity::class.simpleName,
            "authorizationCodeFlowResults authError ex ${authorizationCodeFlowResults?.authError}"
        )



        endSessionFlowResults = AuthFlowResultHandler.getLogoutResultFromIntent(intent)

        Log.d(
            UserActivity::class.simpleName,
            "endSessionFlowResults ${Gson().toJson(endSessionFlowResults?.logoutResponse)}"
        )
        Log.d(
            UserActivity::class.simpleName,
            "endSessionFlowResults authError ex ${endSessionFlowResults?.authError}"
        )
    }
}