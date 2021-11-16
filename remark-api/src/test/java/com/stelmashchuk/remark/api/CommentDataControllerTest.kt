package com.stelmashchuk.remark.api

import app.cash.turbine.test
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.CommentOneLevelRoot
import com.stelmashchuk.remark.api.pojo.VoteResponse
import com.stelmashchuk.remark.api.pojo.VoteType
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CommentDataControllerTest {

  @Test
  fun `Verify get 1th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val postComment1 = mockComment("1", "")
    val postComment2 = mockComment("2", "")
    val service = mockk<RemarkService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(postComment1, mockComment("3", "2"), postComment2)
        )
      }
    }

    CommentDataController(postUrl, service)
        .observeComments(CommentRoot.Post(postUrl))
        .test {
          awaitItem() shouldBe FullCommentInfo(null, listOf(
              CommentInfo(postComment1, 0),
              CommentInfo(postComment2, 1)
          ))
        }
  }

  @Test
  fun `Verify get 2th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = "aa-aa"
    val rootComment = mockComment(rootCommentId, "")
    val comment1 = mockComment("1", rootCommentId)
    val comment2 = mockComment("2", rootCommentId)
    val service = mockk<RemarkService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1, comment2)
        )
      }
    }

    CommentDataController(postUrl, service)
        .observeComments(CommentRoot.Comment(postUrl, rootCommentId))
        .test {
          awaitItem() shouldBe FullCommentInfo(CommentInfo(rootComment, 2), listOf(
              CommentInfo(comment1, 0),
              CommentInfo(comment2, 0)
          ))
        }
  }

  @Test
  fun `Verify vote apply for 2th level comment`(): Unit = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = "aa-aa"
    val voteCommentId = "1"
    val rootComment = mockComment(rootCommentId, "")
    val comment1 = mockComment(voteCommentId, rootCommentId, 1, 0)
    val comment2 = mockComment("2", rootCommentId)
    val service = mockk<RemarkService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1, comment2)
        )
      }
      coEvery { vote(voteCommentId, postUrl, match { it in VoteType.values().map { voteType -> voteType.backendCode } }) } coAnswers {
        VoteResponse(voteCommentId, 2)
      }
    }

    val dataController = CommentDataController(postUrl, service)

    dataController.observeComments(CommentRoot.Comment(postUrl, rootCommentId))
        .test {
          awaitItem() shouldBe FullCommentInfo(CommentInfo(rootComment, 2), listOf(
              CommentInfo(comment1, 0),
              CommentInfo(comment2, 0)
          ))

          dataController.vote(voteCommentId, postUrl, VoteType.UP)

          awaitItem() shouldBe FullCommentInfo(CommentInfo(rootComment, 2), listOf(
              CommentInfo(comment1.copy(score = 2, vote = 1), 0),
              CommentInfo(comment2, 0)
          ))

          dataController.vote(voteCommentId, postUrl, VoteType.DOWN)

          awaitItem() shouldBe FullCommentInfo(CommentInfo(rootComment, 2), listOf(
              CommentInfo(comment1.copy(score = 2, vote = -1), 0),
              CommentInfo(comment2, 0)
          ))
        }
  }


  private fun mockComment(mockId: String, mockParentId: String, mockScore: Int = 0, mockVote: Int = 0): Comment {
    return Comment(
        id = mockId,
        parentId = mockParentId,
        text = "",
        score = mockScore.toLong(),
        vote = mockVote,
        user = mockk(),
        time = ""
    )
  }
}
