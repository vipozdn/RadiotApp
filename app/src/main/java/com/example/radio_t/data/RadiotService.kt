package com.example.radio_t.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadiotService {

  @GET("site-api/last/{posts}")
  suspend fun getPosts(@Path("posts") posts: Long, @Query("categories") type: String = "podcast"): List<Podcast>

}
