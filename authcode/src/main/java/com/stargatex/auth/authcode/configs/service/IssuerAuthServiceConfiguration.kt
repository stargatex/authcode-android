package com.stargatex.auth.authcode.configs.service

import android.content.Context
import android.util.Log
import com.stargatex.auth.authcode.auth.AuthStateManager
import com.stargatex.auth.authcode.configs.auth.AuthConfiguration
import com.stargatex.auth.authcode.extensions.convertToUri
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import net.openid.appauth.AuthorizationServiceConfiguration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal class IssuerAuthServiceConfiguration(
    var context: Context,
    private var authConfiguration: AuthConfiguration
) : AuthServiceConfiguration {
    override suspend fun getAuthorizationServiceConfiguration()
            : AuthorizationServiceConfiguration = suspendCoroutine { continuation ->

        AuthorizationServiceConfiguration.fetchFromIssuer(this.authConfiguration.getOidcConfig().issueUri.convertToUri()) { config, except ->
            when {
                config != null -> {
                    AuthStateManager.getInstance(context, config)
                    Log.d(
                        IssuerAuthServiceConfiguration::class.simpleName,
                        "Authorization Service Configuration retrieved"
                    )
                    continuation.resume(value = config)
                }
                else -> {
                    if (except != null) {
                        continuation.resumeWithException(
                            exception = AuthFlowExceptionHandler.toCustomAuthException(
                                except
                            )
                        )
                    }
                }
            }
        }
    }
}