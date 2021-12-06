package com.stelmashchuk.remark.api.user

import com.stelmashchuk.remark.api.JWT_PREFIX
import com.stelmashchuk.remark.api.SystemStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class RemarkCredentials(
    val jwtToken: String,
    val xsrfToken: String,
) {
  fun isValid(): Boolean {
    return jwtToken.isNotBlank() && xsrfToken.isNotBlank()
  }
}

class LoginFail : Exception()

@OptIn(ExperimentalSerializationApi::class)
public class UserRepository internal constructor(
    private val systemStorage: SystemStorage,
    private val credentialCreator: CredentialCreator,
    private val userService: UserService,
) {

  var user: User? = null
    get() {
      return systemStorage.getString(KEY_USER).takeIf { it.isNotBlank() }?.let { Json.decodeFromString(it) }
    }
    private set(value) {
      systemStorage.putString(KEY_USER, Json.encodeToString(value))
      field = value
    }

  /**
   * @return Result<User>
   */
  suspend fun loginUser(cookies: String): Result<User> {
    val remarkCredentials = credentialCreator.tryCreate(cookies)
        ?: return Result.failure(LoginFail())
    return Result.runCatching { userService.getUser(remarkCredentials.xsrfToken, "$JWT_PREFIX${remarkCredentials.jwtToken}") }
        .onSuccess {
          user = it
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
    user = null
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
    private const val KEY_USER = "KEY_USER"
    private const val KEY_JWT_TOKEN = "KEY_JWT_TOKEN"
    private const val KEY_XSRF_TOKEN = "KEY_XSRF_TOKEN"
  }
}
