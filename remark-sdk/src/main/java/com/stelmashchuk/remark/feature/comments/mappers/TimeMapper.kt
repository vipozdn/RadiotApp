package com.stelmashchuk.remark.feature.comments.mappers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class TimeMapper {

  private val backendFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
  private val uiFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm ")

  fun map(time: LocalDateTime): String {
    return time.format(uiFormatter)
  }
}
