package com.stargatex.auth.authcode.model.exception

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
@Parcelize
public sealed class AuthException(
    public open var code: Int?,
    override var message: String?,
    public open var description: String?,
    public open var uri: String?
) :
    Exception(), Parcelable {
    @Parcelize
    public class ServerException(
        override var code: Int? = -1,
        override var message: String?,
        override var description: String?,
        override var uri: String?
    ) : AuthException(code, message, description, null)

    @Parcelize
    public class ClientException(
        override var code: Int?,
        override var message: String?,
        override var description: String?
    ) : AuthException(code, message, description, null)
}

