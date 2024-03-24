package com.stargatex.auth.authcode.model.auth

/**
 * Specifies the OpenID Connect 1.0 `display` parameter.
 *
 * Specifies the OpenID Connect 1.0 `login_hint` parameter.
 *
 * Specifies additional parameters.
 */
public data class AuthRequestOptionalConfig(
    val mDisplay: String? = null,
    val mLoginHint: String? = null,
    val mAdditionalParameters: Map<String, String>? = null,
)
