package com.stelmashchuk.remark.api.config

import com.stelmashchuk.remark.api.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentOneLevelRoot(
    @SerialName("comments")
    val comments: List<Comment>,
)

@Serializable
data class Comment(
    @SerialName("id")
    val id: String,
    @SerialName("pid")
    val parentId: String,
    @SerialName("orig")
    val text: String = "",
    @SerialName("score")
    val score: Long,
    @SerialName("user")
    val user: User,
    @SerialName("time")
    val time: String,
    @SerialName("vote")
    val vote: Int,
)

@Serializable
data class PostComment(
    @SerialName("text")
    val text: String,
    @SerialName("pid")
    val parentId: String? = null,
    @SerialName("locator")
    val locator: Locator,
)

@Serializable
data class DeletedComment(
    @SerialName("id")
    val id: String,
)

@Serializable
data class Locator(
    @SerialName("site")
    val site: String,
    @SerialName("url")
    val postUrl: String,
)

@Serializable
data class EditCommentRequest(
    @SerialName("delete")
    val delete: Boolean,
)

@Serializable
data class VoteResponse(
    @SerialName("id")
    val id: String,
    @SerialName("score")
    val score: Long,
)

enum class VoteType(val backendCode: Int) {
  UP(1),
  DOWN(-1)
}
