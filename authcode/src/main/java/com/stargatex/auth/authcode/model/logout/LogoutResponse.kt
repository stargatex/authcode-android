package com.stargatex.auth.authcode.model.logout

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public data class LogoutResponse(val logoutRequest: LogoutRequest? = null, val state: String? = null) :
    Parcelable
