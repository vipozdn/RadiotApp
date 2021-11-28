package com.stelmashchuk.remark.feature.comments

import app.cash.turbine.test
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.FullComment
import com.stelmashchuk.remark.api.FullCommentInfo
import com.stelmashchuk.remark.feature.comments.mappers.CommentUiMapper
import com.stelmashchuk.remark.feature.comments.mappers.SingleCommentMapper
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CommentViewModelTest {

  @Test
  fun `Verify view model can return just root comment`() = runBlocking {
    val commentRoot = mockk<CommentRoot>()

    val mockRoot = mockk<FullComment>()
    val mockRootUi = mockk<CommentUiModel>()

    val mapper = mockk<SingleCommentMapper> {
      every { map(mockRoot) } answers { mockRootUi }
    }

    val commentDataController = mockk<CommentDataController> {
      coEvery { observeComments(commentRoot) } coAnswers {
        flow {
          emit(FullCommentInfo(rootComment = mockRoot, comments = emptyList()))
        }
      }
    }

    val viewModel = CommentViewModel(commentRoot, CommentUiMapper(mapper), commentDataController)

    viewModel
        .comments
        .test {
          awaitItem() shouldBe CommentUiState.Data(
              FullCommentsUiModel(mockRootUi, emptyList())
          )
        }
  }
}
