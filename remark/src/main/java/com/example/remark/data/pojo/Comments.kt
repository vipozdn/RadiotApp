package com.example.remark.data.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comments(
    val comments: List<CommentWrapper>,
)

@Serializable
data class CommentWrapper(
    val comment: Comment,
    val replies: List<CommentWrapper> = emptyList(),
)

@Serializable
data class Comment(
    val id : String,
    @SerialName("orig")
    val text: String = "",
    val score: Long,
    val user: User,
    val time: String,
    val vote: Int,
)

@Serializable
data class User(
    val name: String,
)