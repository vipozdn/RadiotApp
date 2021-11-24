package com.stelmashchuk.remark.api.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("picture")
    val avatar: String,
)
