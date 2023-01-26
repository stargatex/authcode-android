package com.stargatex.auth.authcode.configs.client

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
internal object ClientAuthFactory {
    fun getClientAuth(clientSecretConfig: ClientSecretConfig): ClientAuthConfig {
        return when (clientSecretConfig) {
            is ClientPostSecretConfig -> ClientPostAuthConfig(clientSecretConfig.clientSecret)
            is ClientBasicSecretConfig -> ClientBasicAuthConfig(clientSecretConfig.clientSecret)
        }
    }
}