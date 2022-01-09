package com.stelmashchuk.remark.feature.post.integration

import app.cash.turbine.test
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.EditCommentUseCase
import com.stelmashchuk.remark.feature.post.EditMode
import com.stelmashchuk.remark.feature.post.PostComment
import com.stelmashchuk.remark.feature.post.PostCommentStorage
import com.stelmashchuk.remark.feature.post.PostCommentViewModel
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class EditComentFlowIntegationTest {

  @Test
  fun `Verify edit exist comment`() = runBlocking {
    val commentId = CommentId("commentId")
    val text = "comment_text"

    val editCommentUseCase = mockk<EditCommentUseCase> {
      coEvery { editComment(commentId, text) } coAnswers { Result.success(Unit) }
    }

    val editMode = EditMode()
    val storage = PostCommentStorage()

    val postComment = PostComment(
        postCommentUseCase = mockk(),
        postCommentStorage = storage,
        editCommentUseCase = editCommentUseCase,
        editMode = editMode,
    )

    val viewModel = PostCommentViewModel(postComment = postComment, storage)

    editMode.startEditMode(commentId)

    viewModel.updateText(text)
    viewModel.postComment()

    coVerify(exactly = 1) { editCommentUseCase.editComment(commentId, text) }

    storage.flowText().test {
      awaitItem() shouldBe ""
    }

    editMode.flowEditCommentId().test {
      awaitItem() shouldBe null
    }
  }
}
