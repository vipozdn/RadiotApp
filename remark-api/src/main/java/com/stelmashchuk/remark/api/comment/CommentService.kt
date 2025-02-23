package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.config.Config
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CommentService {

  @GET("/api/v1/find")
  suspend fun getCommentsPlain(
      @Query("url") postUrl: String,
      @Query("sort") sort: String = "-time",
      @Query("format") format: String = "plain",
  ): CommentOneLevelRoot

  @POST("api/v1/comment")
  suspend fun postComment(@Body postComment: PostComment): Comment

  @PUT("/api/v1/vote/{commentId}")
  suspend fun vote(
      @Path("commentId") commentId: CommentId,
      @Query("url") postUrl: String,
      @Query("vote") vote: Int,
  ): VoteResponse

  @PUT("/api/v1/comment/{commentId}")
  suspend fun delete(
      @Path("commentId") commentId: CommentId,
      @Body deleteRequest: DeleteRequest,
      @Query("url") postUrl: String,
  ): DeleteResponse

  @PUT("/api/v1/comment/{commentId}")
  suspend fun edit(
      @Path("commentId") commentId: CommentId,
      @Body editRequest: EditRequest,
      @Query("url") postUrl: String,
  ): Comment

  @GET("api/v1/config")
  suspend fun getConfig(): Config

}

internal object HttpConstants {

  const val UN_AUTH = 401
  const val TOO_MANY_REQUESTS = 429

}


