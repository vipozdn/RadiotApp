package com.example.remark.data

import com.example.remark.RemarkSettings
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RemarkService {

  @GET("/api/v1/find")
  suspend fun getComments(
      @Query("url") postUrl: String,
      @Query("sort") sort: String = RemarkSettings.defaultSorting,
      @Query("format") format: String = "tree",
  ): Comments

  @PUT("/api/v1/vote/{commentId}")
  suspend fun vote(
      @Path("commentId") commentId: String,
      @Query("url") postUrl: String,
      @Query("vote") vote: Int,
  ): VoteResponse

  @GET("api/v1/config")
  suspend fun getConfig(): Config
}

