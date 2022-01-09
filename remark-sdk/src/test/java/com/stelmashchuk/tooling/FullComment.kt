package com.stelmashchuk.tooling

import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.FullComment
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

internal fun mockFullComment(mockIsCurrentUserAuthor: Boolean, mockTime: LocalDateTime): FullComment {
  return mockk {
    every { isCurrentUserAuthor } answers { mockIsCurrentUserAuthor }
    every { time } answers { mockTime }
  }
}

internal fun mockFullComment(
    id: CommentId,
    text: String? = null,
    score: Long? = null,
): FullComment {
  return FullComment(
      id = id,
      score = score ?: 0,
      parentId = CommentId(""),
      text = text ?: "",
      user = mockk(),
      time = mockk(),
      vote = 0,
      replyCount = 0,
      isCurrentUserAuthor = true,
  )
}