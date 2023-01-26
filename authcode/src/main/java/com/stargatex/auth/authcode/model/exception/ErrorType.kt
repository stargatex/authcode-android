package com.stargatex.auth.authcode.model.exception

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public sealed class ErrorType : Parcelable {
    @Parcelize
    public class Network(public var code: Int?) : ErrorType()

    @Parcelize
    public class Http(public var code: Int?) : ErrorType()

    @Parcelize
    public object Unexpected : ErrorType()

    @Parcelize
    public object Undefined : ErrorType()
}
