package com.stelmashchuk.remark.data.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comments(
    @SerialName("comments")
    val comments: List<CommentWrapper>,
)

@Serializable
data class CommentWrapper(
    @SerialName("comment")
    val comment: Comment,
    @SerialName("replies")
    val replies: List<CommentWrapper> = emptyList(),
)

@Serializable
data class Comment(
    @SerialName("id")
    val id: String,
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
data class User(
    @SerialName("name")
    val name: String,
    @SerialName("picture")
    val avatar: String,
)
