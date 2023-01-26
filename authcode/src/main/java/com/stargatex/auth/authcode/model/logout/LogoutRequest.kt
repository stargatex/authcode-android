package com.stargatex.auth.authcode.model.logout

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public data class LogoutRequest(
    val mIdTokenHint: String? = null,
    val mPostLogoutRedirectUri: Uri? = null,
    val mState: String? = null, val mUiLocales: String? = null
) : Parcelable