package com.stelmashchuk.remark.api.repositories

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager

data class RemarkCredentials(
    val jwtToken: String,
    val xsrfToken: String,
) {
  fun isValid(): Boolean {
    return jwtToken.isNotBlank() && xsrfToken.isNotBlank()
  }
}

public class UserStorage internal constructor(
    private val sharedPreferences: SharedPreferences,
    private val credentialCreator: CredentialCreator = CredentialCreator(),
) {

  /**
   * @return true if credential save success
   */
  fun saveByCookies(cookies: String): Boolean {
    credentialCreator.tryCreate(cookies)?.let {
      save(it)
      return true
    } ?: let {
      return false
    }
  }

  private fun save(remarkCredentials: RemarkCredentials) {
    sharedPreferences.edit()
        .putString(KEY_JWT_TOKEN, remarkCredentials.jwtToken)
        .putString(KEY_XSRF_TOKEN, remarkCredentials.xsrfToken)
        .apply()
  }

  fun logout() {
    Handler(Looper.getMainLooper()).post {
      CookieManager.getInstance().removeAllCookies {

      }
    }
    save(RemarkCredentials("", ""))
  }

  fun getCredential(): RemarkCredentials {
    return RemarkCredentials(
        sharedPreferences.getString(KEY_JWT_TOKEN, "") ?: "",
        sharedPreferences.getString(KEY_XSRF_TOKEN, "") ?: ""
    )
  }

  fun addListener(onCredentialsUpdate: (RemarkCredentials) -> Unit) {
    onCredentialsUpdate(getCredential())
    sharedPreferences.registerOnSharedPreferenceChangeListener { _, _ ->
      onCredentialsUpdate(getCredential())
    }
  }

  companion object {
    private const val KEY_JWT_TOKEN = "KEY_JWT_TOKEN"
    private const val KEY_XSRF_TOKEN = "KEY_XSRF_TOKEN"
  }
}
