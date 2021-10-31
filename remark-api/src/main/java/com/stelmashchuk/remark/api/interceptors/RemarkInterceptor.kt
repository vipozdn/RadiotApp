package com.stelmashchuk.remark.api.interceptors

import com.stelmashchuk.remark.api.HttpConstants
import com.stelmashchuk.remark.api.repositories.UserStorage
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val KEY_HEADER_XSRF = "X-XSRF-TOKEN"
private const val KEY_COOKIE = "Cookie"
private const val JWT_PREFIX = "JWT="

class RemarkInterceptor(
    private val userStorage: UserStorage,
    private val siteId: String,
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val original = chain.request()

    val request: Request.Builder = original.newBuilder()

    addAuthHeaders(request)
    addSiteId(request, original)

    request.method(original.method, original.body)

    val response = chain.proceed(request.build())

    if (response.code == HttpConstants.UN_AUTH) {
      userStorage.logout()
    }

    return response
  }

  private fun addAuthHeaders(request: Request.Builder) {
    if (userStorage.getCredential().isValid()) {
      val user = userStorage.getCredential()
      request.header(KEY_HEADER_XSRF, user.xsrfToken)
      request.header(KEY_COOKIE, "$JWT_PREFIX${user.jwtToken}")
    }
  }

  private fun addSiteId(request: Request.Builder, original: Request) {
    request.url(original.url.newBuilder()
        .addQueryParameter("site", siteId)
        .build())
  }
}
