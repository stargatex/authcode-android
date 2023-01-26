package com.stargatex.auth.authcode.configs.service

import net.openid.appauth.AuthorizationServiceConfiguration

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal interface AuthServiceConfiguration {
    suspend fun getAuthorizationServiceConfiguration()
            : AuthorizationServiceConfiguration
}