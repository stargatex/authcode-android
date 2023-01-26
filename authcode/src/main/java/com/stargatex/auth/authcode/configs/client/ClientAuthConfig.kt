package com.stargatex.auth.authcode.configs.client

import net.openid.appauth.ClientAuthentication

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public interface ClientAuthConfig {
     public fun clientAuthentication(): ClientAuthentication
}