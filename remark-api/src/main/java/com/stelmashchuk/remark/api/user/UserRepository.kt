package com.stelmashchuk.remark.api.user

import com.stelmashchuk.remark.api.JWT_PREFIX
import com.stelmashchuk.remark.api.SystemStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal data class RemarkCredentials(
    val jwtToken: String,
    val xsrfToken: String,
) {
  fun isValid(): Boolean {
    return jwtToken.isNotBlank() && xsrfToken.isNotBlank()
  }
}

internal class LoginFail : Exception()

@OptIn(ExperimentalSerializationApi::class)
public class UserRepository internal constructor(
    private val systemStorage: SystemStorage,
    private val credentialCreator: CredentialCreator,
    private val userService: UserService,
) {

  internal var user: User? = null
    get() {
      return systemStorage.getString(KEY_USER).takeIf { it.isNotBlank() }?.let { Json.decodeFromString(it) }
    }
    private set(value) {
      systemStorage.putString(KEY_USER, Json.encodeToString(value))
      field = value
    }

  /**
   * The method try to find JWT and XSRF-TOKEN than try to login user into remark42.
   * Save user data into local cache (persistence storage). Use logout for clear user data.
   * @see logout
   * @param cookies all cookies(without any filter and mapping)
   * @return Result.failure(LoginFail()) in case when cookies not valid. Can be result with network error. Result.success after successfully login user.
   */
  public suspend fun loginUser(cookies: String): Result<User> {
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

  /**
   * @see loginUser
   */
  internal fun logout() {
    user = null
    save(RemarkCredentials("", ""))
  }

  internal fun getCredential(): RemarkCredentials {
    return RemarkCredentials(
        systemStorage.getString(KEY_JWT_TOKEN),
        systemStorage.getString(KEY_XSRF_TOKEN)
    )
  }

  internal fun addListener(onCredentialsUpdate: (RemarkCredentials) -> Unit) {
    onCredentialsUpdate(getCredential())
    systemStorage.onValueChanges {
      onCredentialsUpdate(getCredential())
    }
  }

  private companion object {
    const val KEY_USER = "KEY_USER"
    const val KEY_JWT_TOKEN = "KEY_JWT_TOKEN"
    const val KEY_XSRF_TOKEN = "KEY_XSRF_TOKEN"
  }
}
