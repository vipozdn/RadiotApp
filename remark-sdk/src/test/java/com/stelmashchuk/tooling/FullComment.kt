package com.stelmashchuk.tooling

import com.stelmashchuk.remark.api.FullComment
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

fun mockFullComment(mockIsCurrentUserAuthor: Boolean, mockTime: LocalDateTime): FullComment {
  return mockk {
    every { isCurrentUserAuthor } answers { mockIsCurrentUserAuthor }
    every { time } answers { mockTime }
  }
}