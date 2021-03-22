package com.stelmashchuk.remark.data.repositories

import com.stelmashchuk.remark.data.RemarkService
import com.stelmashchuk.remark.data.pojo.Comment
import com.stelmashchuk.remark.data.pojo.CommentWrapper
import com.stelmashchuk.remark.data.pojo.Comments
import com.stelmashchuk.remark.data.pojo.VoteResponse
import com.stelmashchuk.remark.data.pojo.VoteType
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CommentRepositoryTest {

  @Test
  fun `Verify change up vote`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<com.stelmashchuk.remark.data.RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockRootCommentTree(commentId, score = 0, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.UP.backendCode) } coAnswers {
        VoteResponse(commentId, 1)
      }
    }
    val commentRepository = CommentRepository(remarkService)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.UP)
      result.comments.first().comment.score shouldBe 1
      result.comments.first().comment.vote shouldBe 1
    }
  }

  @Test
  fun `Verify change down vote`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<com.stelmashchuk.remark.data.RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockRootCommentTree(commentId, score = 6, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.DOWN.backendCode) } coAnswers {
        VoteResponse(commentId, 5)
      }
    }
    val commentRepository = CommentRepository(remarkService)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.DOWN)
      result.comments.first().comment.score shouldBe 5
      result.comments.first().comment.vote shouldBe -1
    }
  }

  @Test
  fun `Verify change down vote second level`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<com.stelmashchuk.remark.data.RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockSecondLevelCommentTree(commentId, score = 2, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.DOWN.backendCode) } coAnswers {
        VoteResponse(commentId, 1)
      }
    }
    val commentRepository = CommentRepository(remarkService)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.DOWN)
      result.comments.first().replies.first().comment.score shouldBe 1
      result.comments.first().replies.first().comment.vote shouldBe -1
    }
  }

  private fun mockSecondLevelCommentTree(commentId: String, score: Long, vote: Int) = Comments(
      listOf(
          CommentWrapper(
              comment = Comment(
                  id = "commentId",
                  score = -1,
                  vote = -1,
                  text = "",
                  user = mockk(),
                  time = "",
              ),
              replies = listOf(
                  CommentWrapper(
                      comment = Comment(
                          id = commentId,
                          score = score,
                          vote = vote,
                          text = "",
                          user = mockk(),
                          time = "",
                      )
                  )
              )
          )
      )
  )

  private fun mockRootCommentTree(commentId: String, score: Long, vote: Int) = Comments(
      listOf(
          CommentWrapper(
              comment = Comment(
                  id = commentId,
                  score = score,
                  vote = vote,
                  text = "",
                  user = mockk(),
                  time = "",
              ),
              replies = emptyList()
          )
      )
  )
}
