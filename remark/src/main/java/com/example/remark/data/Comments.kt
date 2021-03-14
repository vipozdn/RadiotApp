package com.example.remark.data

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
    @SerialName("orig")
    val text: String,
    val score: Long,
    val user: User,
)

@Serializable
data class User(
    val name: String,
)