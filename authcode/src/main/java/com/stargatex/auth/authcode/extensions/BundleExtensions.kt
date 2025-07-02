package com.stargatex.auth.authcode.extensions

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * @author Lahiru Jayawickrama (stargatex)
 * @version 1.0.0
 */

internal inline fun <reified T : Parcelable> Bundle.safeGetParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}

internal inline fun <reified T : Parcelable> Intent.safeGetParcelableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key)
    }
}