package com.stelmashchuk.remark.data.pojo

import kotlinx.serialization.Serializable

@Serializable
data class VoteResponse(
    val id: String,
    val score: Long,
)

enum class VoteType(val backendCode: Int) {
  UP(1),
  DOWN(-1)
}
