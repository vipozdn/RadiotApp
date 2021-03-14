package com.example.radio_t.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Podcast(
    val url: String,
    val title: String,
    @SerialName("show_num")
    val number: Long,
    @SerialName("time_labels")
    val timeLabels: List<TimeLabels>,
)

@Serializable
data class TimeLabels(
    val topic: String,
)
