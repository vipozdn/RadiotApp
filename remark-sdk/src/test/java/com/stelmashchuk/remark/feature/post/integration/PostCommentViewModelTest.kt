package com.stelmashchuk.remark.feature.post.integration

import app.cash.turbine.test
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.api.comment.PostCommentUseCase
import io.kotlintest.shouldBe
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class PostCommentViewModelTest {

  @Test
  fun `Verify isIconVisible update correctly`() = runBlocking {
    val viewModel = createViewModel()

    viewModel.isIconVisible
        .test {
          awaitItem() shouldBe false

          viewModel.updateText("aa")
          awaitItem() shouldBe true

          viewModel.updateText("")
          awaitItem() shouldBe false
          viewModel.updateText("")

          viewModel.updateText("aa")
          awaitItem() shouldBe true
        }
  }

  @Test
  fun `Verify post new comment`() = runBlocking {
    val commentRoot = mockk<CommentRoot>(relaxed = true)
    val postCommentUseCase = mockk<PostCommentUseCase>()
    val viewModel = createViewModel(postCommentUseCase = postCommentUseCase, commentRoot = commentRoot)

    val text = "comment_text"
    viewModel.updateText(text)
    viewModel.postComment()

    coVerify(exactly = 1) { postCommentUseCase.postComment(commentRoot, text) }
  }
}
