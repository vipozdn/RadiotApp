package com.stelmashchuk.remark.api.network

import com.stelmashchuk.remark.api.pojo.CommentOneLevelRoot
import com.stelmashchuk.remark.api.pojo.Config
import com.stelmashchuk.remark.api.pojo.VoteResponse
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RemarkService {

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

object HttpConstants {

  const val UN_AUTH = 401
  const val TOO_MANY_REQUESTS = 429

}


