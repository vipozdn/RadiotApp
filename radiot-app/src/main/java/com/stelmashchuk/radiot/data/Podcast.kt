package com.stelmashchuk.radiot.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Podcast(
    @SerialName("url")
    val url: String,
    @SerialName("title")
    val title: String,
    @SerialName("show_num")
    val number: Long,
    @SerialName("time_labels")
    val timeLabels: List<TimeLabels>? = null,
)

@Serializable
data class TimeLabels(
    @SerialName("topic")
    val topic: String,
)

@Serializable
data class Theme(
    @SerialName("url")
    val url: String,
    @SerialName("title")
    val title: String,
    val number: Long = title.split(' ').last().toLong(),
)
