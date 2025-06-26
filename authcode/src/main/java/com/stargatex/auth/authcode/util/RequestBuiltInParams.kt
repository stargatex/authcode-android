package com.stargatex.auth.authcode.util

/**
 * @author Lahiru Jayawickrama (stargatex)
 * @version 1.0.0
 */
public object RequestBuiltInParams {
    public const val PARAM_CLIENT_ID: String = "client_id"
    public const val PARAM_CODE_CHALLENGE: String = "code_challenge"
    public const val PARAM_CODE_CHALLENGE_METHOD: String = "code_challenge_method"
    public const val PARAM_DISPLAY: String = "display"
    public const val PARAM_LOGIN_HINT: String = "login_hint"
    public const val PARAM_PROMPT: String = "prompt"
    public const val PARAM_UI_LOCALES: String = "ui_locales"
    public const val PARAM_REDIRECT_URI: String = "redirect_uri"
    public const val PARAM_RESPONSE_MODE: String = "response_mode"
    public const val PARAM_RESPONSE_TYPE: String = "response_type"
    public const val PARAM_SCOPE: String = "scope"
    public const val PARAM_STATE: String = "state"
    public const val PARAM_NONCE: String = "nonce"
    public const val PARAM_CLAIMS: String = "claims"
    public const val PARAM_CLAIMS_LOCALES: String = "claims_locales"

    internal val reservedOidcParams = setOf(
        PARAM_CLIENT_ID,
        PARAM_CODE_CHALLENGE,
        PARAM_CODE_CHALLENGE_METHOD,
        PARAM_DISPLAY,
        PARAM_LOGIN_HINT,
        PARAM_PROMPT,
        PARAM_UI_LOCALES,
        PARAM_REDIRECT_URI,
        PARAM_RESPONSE_MODE,
        PARAM_RESPONSE_TYPE,
        PARAM_SCOPE,
        PARAM_STATE,
        PARAM_NONCE,
        PARAM_CLAIMS,
        PARAM_CLAIMS_LOCALES
    )

    internal val supportedOidcParamsByBuilder = setOf(
        PARAM_PROMPT,
        PARAM_LOGIN_HINT
    )

    internal val unsupportedReservedParams = reservedOidcParams - supportedOidcParamsByBuilder
}