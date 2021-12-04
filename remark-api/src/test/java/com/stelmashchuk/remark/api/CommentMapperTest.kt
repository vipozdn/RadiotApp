package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.comment.CommentMapper
import com.stelmashchuk.remark.api.comment.Comment
import com.stelmashchuk.tooling.getUserRepository
import com.stelmashchuk.tooling.mockkUser
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class CommentMapperTest {

  @Test
  fun `Verify create full comment`() {
    val commentMapper = CommentMapper(mockk(relaxed = true), mockk(relaxed = true))

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

  @Test
  fun `Verify create full comment(check isCurrentUserAuthor)`() {
    val userId = "userId"
    val commentMapper = CommentMapper(mockk(relaxed = true), getUserRepository(mockkUser(userId)))

    val comments = listOf<Comment>(
        mockk(relaxed = true) {
          every { id } answers { "1" }
          every { user } answers { mockkUser("1") }
        },
        mockk(relaxed = true) {
          every { id } answers { "2" }
          every { user } answers { mockkUser(userId) }
        },
    )

    val result = commentMapper.mapCommentsFullComments(comments)

    result.find { it.id == "1" }?.isCurrentUserAuthor shouldBe false
    result.find { it.id == "2" }?.isCurrentUserAuthor shouldBe true
  }

}