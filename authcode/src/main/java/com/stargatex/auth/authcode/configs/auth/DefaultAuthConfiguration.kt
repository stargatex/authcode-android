package com.stargatex.auth.authcode.configs.auth

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stargatex.auth.authcode.R
import com.stargatex.auth.authcode.configs.client.ClientAuthConfig
import com.stargatex.auth.authcode.configs.client.ClientAuthFactory
import com.stargatex.auth.authcode.configs.client.ClientSecretConfig
import com.stargatex.auth.authcode.model.auth.AuthRequestOptionalConfig
import com.stargatex.auth.authcode.model.exception.AuthException
import com.stargatex.auth.authcode.model.exception.AuthFlowExceptionHandler
import com.stargatex.auth.authcode.model.ocid.OidcConfig
import okio.IOException
import okio.buffer
import okio.source
import org.json.JSONException

/**
 * @author Lahiru J (lahirujay)
 * @version 1.0
 */
public class DefaultAuthConfiguration @JvmOverloads constructor(
    private val context: Context, override val clientSecretConfig: ClientSecretConfig? = null,
    override val authRequestOptionalConfig: AuthRequestOptionalConfig? = null,
) : AuthConfiguration {

    private lateinit var oidcConfig: OidcConfig

    init {
        try {
            retrieveConfigs(R.raw.ocid)
        } catch (ex: AuthException) {
            Log.d(
                DefaultAuthConfiguration::class.simpleName,
                "${ex.message} : ${ex.description}"
            )
        }
    }

    private fun retrieveConfigs(resourceId: Int) {
        try {
            val configJsonString =
                context.resources.openRawResource(resourceId).source().buffer().use {
                    it.readUtf8()
                }

            oidcConfig = Gson().fromJson(configJsonString, OidcConfig::class.java)
        } catch (ex: IOException) {
            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Config file reading error",
                ex.localizedMessage
            )
        } catch (ex: JSONException) {
            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Config to parsing error",
                ex.localizedMessage
            )
        } catch (ex: Exception) {
            throw AuthException.ClientException(
                AuthFlowExceptionHandler.AUTH_CLIENT_ERROR,
                "Config retrieve error",
                ex.localizedMessage
            )
        }
    }

    override fun getOidcConfig(): OidcConfig {
        return oidcConfig
    }

    override val clientAuthConfig: ClientAuthConfig?
        get() = clientSecretConfig?.let { ClientAuthFactory.getClientAuth(it) }

}