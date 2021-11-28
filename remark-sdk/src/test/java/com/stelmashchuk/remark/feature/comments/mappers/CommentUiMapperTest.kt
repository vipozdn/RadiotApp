package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.FullComment
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class CommentUiMapperTest {

  @Test
  fun `Verify replyCount null if root without reply`() {
    val comment = mockk<FullComment>(relaxed = true) {
      every { replyCount } answers { 0 }
    }

    SingleCommentMapper(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
        .map(comment).replyCount shouldBe null
  }

  @Test
  fun `Verify replyCount not null if root has replies`() {
    val comment = mockk<FullComment>(relaxed = true) {
      every { replyCount } answers { 10 }
    }

    SingleCommentMapper(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
        .map(comment).replyCount shouldBe 10
  }

}