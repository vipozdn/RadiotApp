package com.stelmashchuk.remark.feature.post

import app.cash.turbine.test
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.CommentRoot
import io.kotlintest.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class PostCommentViewModelTest {

  private val commentRoot = mockk<CommentRoot>()

  @Test
  fun `Verify isIconVisible update correctly`() = runBlocking {
    val commentDataController: CommentDataController = mockk()
    val viewModel = PostCommentViewModel(commentRoot, commentDataController)

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
}
