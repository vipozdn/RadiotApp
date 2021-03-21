package com.example.remark.data

import android.content.SharedPreferences

data class RemarkCredentials(
    val jwtToken: String,
    val xsrfToken: String,
) {
  fun isValid(): Boolean {
    return jwtToken.isNotBlank() && xsrfToken.isNotBlank()
  }
}

class UserStorage(private val sharedPreferences: SharedPreferences) {

  fun save(remarkCredentials: RemarkCredentials) {
    sharedPreferences.edit()
        .putString(KEY_JWT_TOKEN, remarkCredentials.jwtToken)
        .putString(KEY_XSRF_TOKEN, remarkCredentials.xsrfToken)
        .apply()
  }

  fun getCredential(): RemarkCredentials {
    return RemarkCredentials(
        sharedPreferences.getString(KEY_JWT_TOKEN, "") ?: "",
        sharedPreferences.getString(KEY_XSRF_TOKEN, "") ?: ""
    )
  }

  fun addListener(onCredentialsUpdate: (RemarkCredentials) -> Unit) {
    onCredentialsUpdate(getCredential())
    sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
      onCredentialsUpdate(getCredential())
    }
  }

  companion object {
    private const val KEY_JWT_TOKEN = "KEY_JWT_TOKEN"
    private const val KEY_XSRF_TOKEN = "KEY_XSRF_TOKEN"
  }

}