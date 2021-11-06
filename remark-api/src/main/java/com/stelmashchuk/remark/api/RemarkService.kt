package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.pojo.*
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RemarkService {

  @GET("/api/v1/find")
  suspend fun getCommentsTree(
      @Query("url") postUrl: String,
      @Query("sort") sort: String = "-active",
      @Query("format") format: String = "tree",
  ): Comments

  @GET("/api/v1/find")
  suspend fun getCommentsPlain(
      @Query("url") postUrl: String,
      @Query("sort") sort: String = "-active",
      @Query("format") format: String = "plain",
  ): CommentOneLevelRoot

  @PUT("/api/v1/vote/{commentId}")
  suspend fun vote(
      @Path("commentId") commentId: String,
      @Query("url") postUrl: String,
      @Query("vote") vote: Int,
  ): VoteResponse

  @GET("api/v1/config")
  suspend fun getConfig(): Config
}

