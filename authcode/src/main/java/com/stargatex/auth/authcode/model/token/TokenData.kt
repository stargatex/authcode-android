package com.stargatex.auth.authcode.model.token

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class TokenData(
    val accessToken: String? = null,
    val accessTokenExpirationTime: Long? = null,
    val refreshToken: String? = null,
    val idToken: String? = null,
    val scope: String? = null,
    val tokenType: String? = null
) : Parcelable