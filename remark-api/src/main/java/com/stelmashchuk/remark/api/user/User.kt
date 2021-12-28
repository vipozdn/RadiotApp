package com.stelmashchuk.remark.api.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class UserId(private val raw: String) {

  override fun toString(): String {
    return raw
  }

}

@Serializable
public data class User(
    @SerialName("id")
    val id: UserId,
    @SerialName("name")
    val name: String,
    @SerialName("picture")
    val avatar: String,
)
