package com.example.remark.data

import com.example.remark.RemarkSettings
import retrofit2.http.GET
import retrofit2.http.Query

interface RemarkService {

  @GET("/api/v1/find")
  suspend fun getComments(
      @Query("url") postUrl: String,
      @Query("site") siteId: String = RemarkSettings.siteId,
      @Query("sort") sort: String = RemarkSettings.defaultSorting,
      @Query("format") format: String = "tree",
  ): Comments

  @GET("api/v1/config")
  suspend fun getConfig(
      @Query("site") siteId: String,
  ): Config
}

