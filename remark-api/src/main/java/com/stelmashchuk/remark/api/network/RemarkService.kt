package com.stelmashchuk.remark.api.network

import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.CommentOneLevelRoot
import com.stelmashchuk.remark.api.pojo.Config
import com.stelmashchuk.remark.api.pojo.DeleteCommentRequest
import com.stelmashchuk.remark.api.pojo.DeletedComment
import com.stelmashchuk.remark.api.pojo.PostComment
import com.stelmashchuk.remark.api.pojo.User
import com.stelmashchuk.remark.api.pojo.VoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RemarkService {

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
      @Path("commentId") commentId: String,
      @Query("url") postUrl: String,
      @Query("vote") vote: Int,
  ): VoteResponse

  @PUT("/api/v1/comment/{commentId}")
  suspend fun delete(
      @Path("commentId") commentId: String,
      @Body deleteCommentRequest: DeleteCommentRequest = DeleteCommentRequest(),
  ): DeletedComment

  @GET("api/v1/config")
  suspend fun getConfig(): Config

  @GET("/api/v1/user")
  suspend fun getUser(): User
}

object HttpConstants {

  const val UN_AUTH = 401
  const val TOO_MANY_REQUESTS = 429

}


