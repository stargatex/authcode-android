package com.stargatex.auth.authcode.extensions

import android.net.Uri
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */

internal fun String.convertToUri(): Uri {
    try {
        return Uri.parse(this)
    } catch (ex: Exception) {
        throw AuthException
            .ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Could not parse : $this",
                ex.localizedMessage
            )
    }
}