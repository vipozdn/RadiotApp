package com.stelmashchuk.remark.api.comment

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentTimeMapper {

  private val backendFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  fun map(time: String): LocalDateTime {
    return LocalDateTime.parse(time, backendFormatter) ?: LocalDateTime.MAX
  }
}
