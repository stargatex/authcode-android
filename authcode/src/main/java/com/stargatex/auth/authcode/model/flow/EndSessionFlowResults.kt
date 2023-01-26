package com.stargatex.auth.authcode.model.flow

import android.os.Parcelable
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.logout.LogoutResponse
import kotlinx.android.parcel.Parcelize


/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public class EndSessionFlowResults(
    public var logoutResponse: LogoutResponse? = null,
    public var authError: AuthException? = null
) : Parcelable