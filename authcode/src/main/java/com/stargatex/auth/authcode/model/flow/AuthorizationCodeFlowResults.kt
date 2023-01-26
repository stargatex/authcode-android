package com.stargatex.auth.authcode.model.flow

import android.os.Parcelable
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.token.TokenData
import kotlinx.android.parcel.Parcelize


/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public class AuthorizationCodeFlowResults(
    public var tokenResult: TokenData? = null,
    public var authError: AuthException? = null
) : Parcelable