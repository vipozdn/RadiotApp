package com.stelmashchuk.remark.feature.delete

import com.stelmashchuk.remark.api.comment.FullComment
import com.stelmashchuk.remark.api.config.ConfigRepository
import com.stelmashchuk.remark.os.OsDateTime
import java.time.Duration

internal class DeleteAvailableChecker(
    private val configRepository: ConfigRepository,
    private val osDateTime: OsDateTime,
) {

  suspend fun check(comment: FullComment?): Long? {
    if (comment == null || !comment.isCurrentUserAuthor) {
      return null
    }

    val commentTime = comment.time
    val osTime = osDateTime.nowUTC()

    val configDelta = configRepository.getConfig().editDuration

    val delta = Duration.between(commentTime, osTime).seconds
    return if (delta < configDelta) {
      configDelta - delta
    } else {
      null
    }
  }
}
