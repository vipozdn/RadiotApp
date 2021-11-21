package com.stelmashchuk.radiot.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadiotService {

  @GET("site-api/last/{posts}")
  suspend fun getPosts(@Path("posts") posts: Long, @Query("categories") type: String = "podcast"): List<Podcast>

  @GET("site-api/last/{posts}")
  suspend fun getThemes(@Path("posts") posts: Long, @Query("categories") type: String = "prep"): List<Theme>

  companion object {
    const val DEF_THEMES_COUNT = 50L
  }
}
