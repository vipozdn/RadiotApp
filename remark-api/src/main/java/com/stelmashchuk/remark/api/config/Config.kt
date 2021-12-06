package com.stelmashchuk.remark.api.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("auth_providers")
    val authProviders: List<String>,

    @SerialName("edit_duration")
    val editDuration: Long,

)
