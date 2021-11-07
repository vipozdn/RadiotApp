package com.stelmashchuk.remark.api.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
