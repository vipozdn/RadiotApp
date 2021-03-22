package com.example.remark.data.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("auth_providers")
    val authProviders: List<String>,
)