package com.example.remark.data

import kotlinx.serialization.Serializable

@Serializable
class VoteResponse(
    val id: String,
    val score: Int,
)

enum class VoteType(val backendCode: Int) {
  UP(1),
  DOWN(-1)
}
