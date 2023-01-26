package com.stargatex.auth.authcode.configs.service

import android.content.Context
import android.util.Log
import com.stargatex.auth.authcode.auth.AuthStateManager
import com.stargatex.auth.authcode.configs.auth.AuthConfiguration
import com.stargatex.auth.authcode.extensions.convertToUri
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import net.openid.appauth.AuthorizationServiceConfiguration

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal class CustomAuthServiceConfiguration(
    var context: Context,
    var authConfiguration: AuthConfiguration
) : AuthServiceConfiguration {

    override suspend fun getAuthorizationServiceConfiguration(): AuthorizationServiceConfiguration {
        val authorizationUri: String = authConfiguration.getOidcConfig().authorizationUri
        val tokenUri = authConfiguration.getOidcConfig().tokenUri
        val registrationUri = authConfiguration.getOidcConfig().registrationUri
        val endSessionUri = authConfiguration.getOidcConfig().endSessionUri

        if (authorizationUri == null) {
            Log.e(
                CustomAuthServiceConfiguration::class.java.simpleName,
                "Authorization Uri not available",
                AuthException.ClientException(
                    AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                    "Authorization Uri JSON property not available",
                    null
                )
            )
            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Authorization Uri JSON property not available",
                null
            )
        }

        if (tokenUri == null) {
            Log.e(
                CustomAuthServiceConfiguration::class.java.simpleName, "Token Uri not available",
                AuthException.ClientException(
                    AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                    "Token Uri JSON property not available",
                    null
                )
            )
            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Token Uri JSON property not available",
                null
            )
        }

        if (registrationUri == null) {
            Log.e(
                CustomAuthServiceConfiguration::class.java.simpleName, "Register Uri not available",
                AuthException.ClientException(
                    AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                    "Register Uri JSON property not available",
                    null
                )
            )

            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Register Uri JSON property not available",
                null
            )
        }

        if (endSessionUri == null) {
            Log.e(
                CustomAuthServiceConfiguration::class.java.simpleName,
                "End Session Uri JSON property not available",
                AuthException.ClientException(
                    AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                    "End Session Uri JSON property not available",
                    null
                )
            )

            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "End Session Uri JSON property not available",
                null
            )
        }

        val authorizationServiceConfiguration = AuthorizationServiceConfiguration(
            authorizationUri.convertToUri(), // authorization endpoint
            tokenUri.convertToUri(), // token endpoint
            registrationUri.convertToUri(), // register endpoint
            endSessionUri.convertToUri() // end Session endpoint
        )
        AuthStateManager.getInstance(context, authorizationServiceConfiguration)
        return authorizationServiceConfiguration
    }
}