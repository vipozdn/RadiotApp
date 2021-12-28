package com.stelmashchuk.remark.api

import com.stelmashchuk.remark.api.comment.Comment
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentMapper
import com.stelmashchuk.remark.api.user.User
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

    val rootCommentId = CommentId("rootCommentId")

    val comments = listOf(
        mockComment(rootCommentId, CommentId("")),
        mockComment(CommentId("mock1"), rootCommentId),
        mockComment(CommentId("mock2"), rootCommentId),
    )

    commentMapper.mapCommentsFullComments(comments).find { it.id == rootCommentId }?.replyCount shouldBe 2
  }

  @Test
  fun `Verify create full comment(check isCurrentUserAuthor)`() {
    val userId = "userId"
    val commentMapper = CommentMapper(mockk(relaxed = true), getUserRepository(mockkUser(userId)))

    val comments = listOf(
        mockComment(id = CommentId("1"), userId = "1"),
        mockComment(id = CommentId("2"), userId = userId),
    )

    val result = commentMapper.mapCommentsFullComments(comments)

    result.find { it.id == CommentId("1") }?.isCurrentUserAuthor shouldBe false
    result.find { it.id == CommentId("2") }?.isCurrentUserAuthor shouldBe true
  }

  private fun mockComment(id: CommentId, parentId: CommentId = CommentId(""), userId: String? = null): Comment {
    val user = mockk<User>(relaxed = true)
    if (userId != null) {
      every { user.id } answers { userId }
    }
    return Comment(
        id = id,
        parentId = parentId,
        text = "",
        score = 0L,
        user = user,
        time = "2021-11-30T13:57:23.308974867-06:00",
        vote = 0,
    )
  }
}
