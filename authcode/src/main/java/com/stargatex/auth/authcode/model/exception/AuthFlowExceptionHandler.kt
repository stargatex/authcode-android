package com.stargatex.auth.authcode.model.exception

import net.openid.appauth.AuthorizationException

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal object AuthFlowExceptionHandler {

    const val AUTH_CLIENT_ERROR = 1
    const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
    const val UNKNOWN_ERROR_TYPE = "UNKNOWN_ERROR_TYPE"


    fun toCustomAuthException(exception: AuthorizationException?): AuthException.ServerException {
        //Log.d(AuthFlowExceptionHandler::class.java.simpleName, "ex " + exception?.message)
        val errorDescriptionPart = mutableListOf<String>()
        if (exception?.error != null) errorDescriptionPart.add(exception.error!!)
        errorDescriptionPart.add(exception?.errorDescription ?: UNKNOWN_ERROR)
        if (exception?.errorUri != null) errorDescriptionPart.add("Uri: ${exception.errorUri}")
        val errorDescription =
            errorDescriptionPart.joinToString(prefix = "{", postfix = "}", separator = "/")

        val message =
            "${exception?.type ?: UNKNOWN_ERROR_TYPE} - ${exception?.code} : $errorDescription  "
        return AuthException.ServerException(
            code = exception?.code,
            message = message,
            description = errorDescription,
            uri = exception?.errorUri?.toString()
        )
    }
}