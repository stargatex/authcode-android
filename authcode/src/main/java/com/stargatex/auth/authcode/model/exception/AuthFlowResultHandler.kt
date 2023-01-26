package com.stargatex.auth.authcode.model.exception

import android.content.Intent
import com.stargatex.auth.authcode.auth.AuthActivity
import com.stargatex.auth.authcode.auth.LogoutActivity
import com.stargatex.auth.authcode.model.flow.AuthorizationCodeFlowResults
import com.stargatex.auth.authcode.model.flow.EndSessionFlowResults

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public class AuthFlowResultHandler {
    public companion object {
        @JvmStatic
        public fun getLoginResultFromIntent(intent: Intent): AuthorizationCodeFlowResults? {
            return AuthActivity.getLoginResultFromIntent(intent)
        }

        @JvmStatic
        public fun getLogoutResultFromIntent(intent: Intent): EndSessionFlowResults? {
            return LogoutActivity.getLogoutResultFromIntent(intent)
        }
    }
}