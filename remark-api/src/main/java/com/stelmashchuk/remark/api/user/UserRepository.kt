package com.stelmashchuk.remark.api.user

import com.stelmashchuk.remark.api.SystemStorage
import com.stelmashchuk.remark.api.network.JWT_PREFIX

data class RemarkCredentials(
    val jwtToken: String,
    val xsrfToken: String,
) {
  fun isValid(): Boolean {
    return jwtToken.isNotBlank() && xsrfToken.isNotBlank()
  }
}

class LoginFail : Exception()

public class UserRepository internal constructor(
    private val systemStorage: SystemStorage,
    private val credentialCreator: CredentialCreator,
    private val userService: UserService,
) {

  /**
   * @return true if credential save success
   */
  suspend fun loginUser(cookies: String): Result<User> {
    val remarkCredentials = credentialCreator.tryCreate(cookies)
        ?: return Result.failure(LoginFail())
    return Result.runCatching { userService.getUser(remarkCredentials.xsrfToken, "$JWT_PREFIX${remarkCredentials.jwtToken}") }
        .onSuccess {
          save(remarkCredentials)
        }
  }

  private fun save(remarkCredentials: RemarkCredentials) {
    systemStorage.putStrings(
        mapOf(
            KEY_JWT_TOKEN to remarkCredentials.jwtToken,
            KEY_XSRF_TOKEN to remarkCredentials.xsrfToken,
        )
    )
  }

  fun logout() {
    save(RemarkCredentials("", ""))
  }

  fun getCredential(): RemarkCredentials {
    return RemarkCredentials(
        systemStorage.getString(KEY_JWT_TOKEN),
        systemStorage.getString(KEY_XSRF_TOKEN)
    )
  }

  fun addListener(onCredentialsUpdate: (RemarkCredentials) -> Unit) {
    onCredentialsUpdate(getCredential())
    systemStorage.onValueChanges {
      onCredentialsUpdate(getCredential())
    }
  }

  companion object {
    private const val KEY_USER_ID = "KEY_USER_ID"
    private const val KEY_JWT_TOKEN = "KEY_JWT_TOKEN"
    private const val KEY_XSRF_TOKEN = "KEY_XSRF_TOKEN"
  }
}
