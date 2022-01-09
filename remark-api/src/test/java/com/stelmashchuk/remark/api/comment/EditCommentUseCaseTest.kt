package com.stelmashchuk.remark.api.comment

import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class EditCommentUseCaseTest {

  @Test
  fun `Verify edit comment`(): Unit = runBlocking {
    val postUrl = "postUrl"

    val storage = CommentStorage()
    val commentId = CommentId("id")
    val comment = FullComment(
        id = commentId,
        parentId = CommentId("parent"),
        text = "oldText",
        score = 0,
        user = mockk(),
        time = mockk(),
        vote = 0,
        replyCount = 0,
        isCurrentUserAuthor = true,
    )

    storage.add(comment)

    val newText = "newText"

    val service = mockCommentService(commentId, newText, postUrl)

    val editCommentUseCase = EditCommentUseCase(
        commentStorage = storage,
        commentService = service,
        postUrl = postUrl,
    )

    editCommentUseCase.editComment(comment.id, newText) shouldBe Result.success(Unit)

    storage.waitForComment(commentId) shouldBe comment.copy(text = newText)
  }

  private fun mockCommentService(id: CommentId, newText: String, postUrl: String): CommentService {
    val comment = Comment(
        id = id,
        parentId = CommentId("parent"),
        text = newText,
        score = 0,
        user = mockk(relaxed = true),
        time = "2021-11-30T13:57:23.308974867-06:00",
        vote = 0,
    )
    return mockk {
      coEvery { edit(commentId = id, editRequest = EditRequest(newText), postUrl = postUrl) } answers { comment }
    }
  }
}
