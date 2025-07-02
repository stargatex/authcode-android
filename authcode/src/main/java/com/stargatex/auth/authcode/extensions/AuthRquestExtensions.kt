package com.stargatex.auth.authcode.extensions

import android.util.Log
import com.stargatex.auth.authcode.util.RequestBuiltInParams
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.BuildConfig

/**
 * @author Lahiru Jayawickrama (stargatex)
 * @version 1.0.0
 */

internal fun AuthorizationRequest.Builder.applyOidcExtras(
    params: Map<String, String>?,
    onUnsupportedReservedParam: ((key: String, value: String) -> Unit)? = null
): AuthorizationRequest.Builder {
    val safeParams = params.orEmpty()

    if (safeParams.isEmpty()) {
        return this
    }

    val reservedParams = mutableMapOf<String, String>()
    val additionalParams = safeParams.toMutableMap()

    RequestBuiltInParams.reservedOidcParams.forEach { key ->
        additionalParams.remove(key)?.let { value ->
            reservedParams[key] = value
        }
    }

    reservedParams[RequestBuiltInParams.PARAM_PROMPT]?.takeIf { it.isNotBlank() }
        ?.let {
            this.setPrompt(it)
        }

    reservedParams[RequestBuiltInParams.PARAM_LOGIN_HINT]?.takeIf { it.isNotBlank() }
        ?.let { this.setLoginHint(it) }

    reservedParams[RequestBuiltInParams.PARAM_SCOPE]?.takeIf { it.isNotBlank() }
        ?.let { this.setScope(it) }

    reservedParams
        .filterKeys { it in RequestBuiltInParams.unsupportedReservedParams }
        .forEach { (key, value) ->
            onUnsupportedReservedParam?.invoke(key, value)
                ?: Log.w(
                    "AuthRequestExtensions",
                    "Unsupported reserved param '$key' is ignored with value '$value'"
                )
        }

    if (additionalParams.isNotEmpty()) {
        this.setAdditionalParameters(additionalParams)
    }

    return this

}