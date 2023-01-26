package com.stargatex.auth.authcode.configs.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public sealed class ClientSecretConfig(public open var clientSecret: String) : Parcelable

@Parcelize
public class ClientPostSecretConfig(override var clientSecret: String) : ClientSecretConfig(clientSecret)

@Parcelize
public class ClientBasicSecretConfig(override var clientSecret: String) : ClientSecretConfig(clientSecret)
