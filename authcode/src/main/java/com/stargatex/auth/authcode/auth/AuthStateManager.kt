package com.stargatex.auth.authcode.auth

import android.content.Context
import android.util.Log
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal object AuthStateManager {

    private var authState: AuthState? = null

    fun getInstance(
        context: Context, authorizationServiceConfiguration: AuthorizationServiceConfiguration
    ) {
        authState = AuthState(authorizationServiceConfiguration)
        Log.i(
            AuthStateManager.javaClass.simpleName,
            "Auth state initialize"
        )
    }

    fun saveToken(tokenResponse: TokenResponse) {
        authState!!.update(tokenResponse, null)
    }

    fun clear() {
        authState = AuthState(authState!!.authorizationServiceConfiguration!!)
    }


}