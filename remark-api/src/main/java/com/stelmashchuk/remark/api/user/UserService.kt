package com.stelmashchuk.remark.api.user

import com.stelmashchuk.remark.api.network.KEY_COOKIE
import com.stelmashchuk.remark.api.network.KEY_HEADER_XSRF
import retrofit2.http.GET
import retrofit2.http.Header

interface UserService {

  @GET("/api/v1/user")
  suspend fun getUser(@Header(KEY_HEADER_XSRF) xsrf: String, @Header(KEY_COOKIE) cookie: String): User

}