package com.stelmashchuk.remark.data.interceptors

import com.stelmashchuk.remark.data.repositories.UserStorage
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val KEY_HEADER_XSRF = "X-XSRF-TOKEN"
private const val KEY_COOKIE = "Cookie"
private const val JWT_PREFIX = "JWT="

class RemarkInterceptor(private val userStorage: UserStorage) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val original = chain.request()

    val request: Request.Builder = original.newBuilder()

    addAuthHeaders(request)
    addSiteId(request, original)

    request.method(original.method, original.body)

    return chain.proceed(request.build())
  }

  private fun addAuthHeaders(request: Request.Builder) {
    if (userStorage.getCredential().isValid()) {
      val user = userStorage.getCredential()
      request.header(KEY_HEADER_XSRF, user.xsrfToken)
      request.header(KEY_COOKIE, "${JWT_PREFIX}${user.jwtToken}")
    }
  }

  private fun addSiteId(request: Request.Builder, original: Request) {
    request.url(original.url.newBuilder()
        .addQueryParameter("site", com.stelmashchuk.remark.RemarkSettings.siteId)
        .build())
  }
}
