package com.stelmashchuk.remark.api.network

import com.stelmashchuk.remark.api.comment.HttpConstants
import com.stelmashchuk.remark.api.user.UserRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

const val KEY_HEADER_XSRF = "X-XSRF-TOKEN"
const val KEY_COOKIE = "Cookie"
const val JWT_PREFIX = "JWT="

internal class RemarkInterceptor(
    private val userRepository: UserRepository,
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
      userRepository.logout()
    }

    return response
  }

  private fun addAuthHeaders(request: Request.Builder) {
    if (userRepository.getCredential().isValid()) {
      val user = userRepository.getCredential()
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
