package com.stargatex.auth.authcode.configs.auth

import com.stargatex.auth.authcode.configs.client.ClientAuthConfig
import com.stargatex.auth.authcode.configs.client.ClientSecretConfig
import com.stargatex.auth.authcode.model.auth.AuthRequestOptionalConfig
import com.stargatex.auth.authcode.model.oidc.OidcConfig

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public interface AuthConfiguration {
    public fun getOidcConfig(): OidcConfig
    public val clientAuthConfig: ClientAuthConfig?
    public val clientSecretConfig: ClientSecretConfig?
    public val authRequestOptionalConfig: AuthRequestOptionalConfig?
}