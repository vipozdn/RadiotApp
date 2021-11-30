package com.stelmashchuk.remark.feature.delete

import com.stelmashchuk.remark.api.FullComment
import com.stelmashchuk.remark.api.config.ConfigRepository
import com.stelmashchuk.remark.os.OsDateTime
import java.time.Duration

internal class DeleteAvailableChecker(
    private val configRepository: ConfigRepository,
    private val osDateTime: OsDateTime,
) {

  suspend fun check(comment: FullComment): Boolean {
    if (!comment.isCurrentUserAuthor) {
      return false
    }

    return Duration.between(comment.time, osDateTime.now()).seconds <= configRepository.getConfig().editDuration
  }
}
