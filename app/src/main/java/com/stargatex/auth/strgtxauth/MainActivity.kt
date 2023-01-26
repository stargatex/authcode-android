package com.stargatex.auth.strgtxauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.stargatex.auth.authcode.auth.DefaultWebAuthProvider
import com.stargatex.auth.authcode.auth.WebAuthProvider
import com.stargatex.auth.authcode.configs.auth.AuthConfiguration
import com.stargatex.auth.authcode.configs.auth.DefaultAuthConfiguration
import com.stargatex.auth.authcode.configs.client.ClientPostSecretConfig
import com.stargatex.auth.authcode.model.exception.AuthFlowResultHandler
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.strgtxauth.util.AuthConstant.CLIENT_SECRET


class MainActivity : AppCompatActivity() {
    lateinit var authConfiguration: AuthConfiguration
    lateinit var webAuthProvider: WebAuthProvider
    private var authorizationCodeFlowResults: AuthorizationCodeFlowResults? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = findViewById<MaterialButton>(R.id.login)
        authorizationCodeFlowResults = AuthFlowResultHandler.getLoginResultFromIntent(intent)
        Log.d(
            MainActivity::class.simpleName,
            "authorizationCodeFlowResults ${authorizationCodeFlowResults?.tokenResult}"
        )

        Log.d(
            MainActivity::class.simpleName,
            "authorizationCodeFlowResults ${authorizationCodeFlowResults?.authError?.message}"
        )

        authConfiguration =
            DefaultAuthConfiguration(context = this, ClientPostSecretConfig(CLIENT_SECRET))
        webAuthProvider =
            DefaultWebAuthProvider(context = this, authConfiguration = authConfiguration)



        login.setOnClickListener {
            (webAuthProvider as DefaultWebAuthProvider).login(
                this,
                onSuccessIntent = Intent(this, UserActivity::class.java),
                onFailIntent = Intent(this, MainActivity::class.java)
            )
        }

    }
}