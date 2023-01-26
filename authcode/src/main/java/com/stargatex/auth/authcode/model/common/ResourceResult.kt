package com.stargatex.auth.authcode.model.common

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */

public sealed class ResourceResult<T> {
   public class Success<T>(public val data: T) : ResourceResult<T>()
   public class Error<T>(public val exception: Exception) : ResourceResult<T>()
   public class Loading<T> : ResourceResult<T>()
}
