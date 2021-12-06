package com.stelmashchuk.remark.api.comment

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class CommentTimeMapper {
  //2021-11-30T13:57:23.308974867-06:00
  private val backendFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

  fun map(time: String): LocalDateTime {
    val withoutTimeZone = LocalDateTime.parse(time.substring(0, time.indexOf('.')), backendFormatter)

    val timeZone = time.substring(time.indexOfLast { it == '-' })
    val dateTimeInMyZone: ZonedDateTime = ZonedDateTime.of(withoutTimeZone, ZoneId.of(timeZone))

    return dateTimeInMyZone
        .withZoneSameInstant(ZoneOffset.UTC)
        .toLocalDateTime()
  }
}
