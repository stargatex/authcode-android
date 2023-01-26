package com.stargatex.auth.authcode.configs.client

import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal class ClientBasicAuthConfig(private var clientSecret: String) : ClientAuthConfig {
    override fun clientAuthentication(): ClientAuthentication = ClientSecretBasic(clientSecret)
}