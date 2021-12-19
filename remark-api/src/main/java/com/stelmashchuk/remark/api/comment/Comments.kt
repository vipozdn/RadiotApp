package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentOneLevelRoot(
    @SerialName("comments")
    val comments: List<Comment>,
)

@Serializable
internal data class Comment(
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
internal data class PostComment(
    @SerialName("text")
    val text: String,
    @SerialName("pid")
    val parentId: String? = null,
    @SerialName("locator")
    val locator: Locator,
)

@Serializable
internal data class DeletedComment(
    @SerialName("id")
    val id: String,
)

@Serializable
internal data class Locator(
    @SerialName("site")
    val site: String,
    @SerialName("url")
    val postUrl: String,
)

@Serializable
internal data class EditCommentRequest(
    @SerialName("delete")
    val delete: Boolean,
)

@Serializable
internal data class VoteResponse(
    @SerialName("id")
    val id: String,
    @SerialName("score")
    val score: Long,
)

public enum class VoteType(public val backendCode: Int) {
  UP(1),
  DOWN(-1)
}
