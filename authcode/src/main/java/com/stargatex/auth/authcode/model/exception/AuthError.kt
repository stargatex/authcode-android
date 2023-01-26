package com.stargatex.auth.authcode.model.exception

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
internal class AuthError(
    var errorType: ErrorType = ErrorType.Undefined,
    var exception: Exception? = null,
    var message: String? = exception?.localizedMessage
) : Parcelable