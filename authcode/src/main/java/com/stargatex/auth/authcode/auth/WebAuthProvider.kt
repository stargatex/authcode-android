package com.stargatex.auth.authcode.auth

import android.content.Context
import android.content.Intent
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public interface WebAuthProvider {
    public fun login(context: Context, onSuccessIntent: Intent, onFailIntent: Intent)
    public fun logout(context: Context, idToken: String, onCompleteIntent: Intent)
    public fun refreshAccessToken(
        context: Context,
        refreshToken: String
    ): AuthorizationCodeFlowResults?
}