package com.stelmashchuk.remark.api.pojo

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
data class User(
    @SerialName("name")
    val name: String,
    @SerialName("picture")
    val avatar: String,
)
