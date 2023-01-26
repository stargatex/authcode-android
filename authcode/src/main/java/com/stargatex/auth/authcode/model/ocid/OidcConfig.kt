package com.stargatex.auth.authcode.model.ocid

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public data class OidcConfig(
    var issueUri: String,
    var clientId: String,
    var redirectUri: String,
    var scope: String,
    var logoutRedirectUri: String,
    var authorizationUri: String,
    var tokenUri: String,
    var endSessionUri: String,
    var registrationUri: String,
)