package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.comment.CommentMapper
import com.stelmashchuk.remark.api.config.Comment
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class CommentMapperTest {

  @Test
  fun `Verify create full comment`() {
    val commentMapper = CommentMapper(mockk(relaxed = true))

    val rootCommentId = "rootCommentId"

    val comments = listOf<Comment>(
        mockk(relaxed = true) {
          every { id } answers { rootCommentId }
          every { parentId } answers { "" }
        },
        mockk(relaxed = true) {
          every { parentId } answers { rootCommentId }
        },
        mockk(relaxed = true) {
          every { parentId } answers { rootCommentId }
        }
    )

    commentMapper.mapCommentsFullComments(comments).find { it.id == rootCommentId }?.replyCount shouldBe 2
  }

}