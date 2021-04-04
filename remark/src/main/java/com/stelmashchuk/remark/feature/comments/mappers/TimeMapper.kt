package com.stelmashchuk.remark.feature.comments.mappers

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class TimeMapper {

  private val backendFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
  private val uiFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm ")

  fun map(time: String): String {
    val localDateTime = LocalDateTime.parse(time, backendFormatter)

    return localDateTime.format(uiFormatter)
  }
}
