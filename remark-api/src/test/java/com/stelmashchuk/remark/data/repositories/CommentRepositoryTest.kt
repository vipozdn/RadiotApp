package com.stelmashchuk.remark.data.repositories

import com.stelmashchuk.remark.api.RemarkService
import com.stelmashchuk.remark.api.pojo.*
import com.stelmashchuk.remark.api.repositories.CommentRepository
import com.stelmashchuk.remark.api.repositories.NotAuthUser
import com.stelmashchuk.remark.api.repositories.UserStorage
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import retrofit2.HttpException

internal class CommentRepositoryTest {

  @Test
  fun `Verify change up vote`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockRootCommentTree(commentId, score = 0, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.UP.backendCode) } coAnswers {
        VoteResponse(commentId, 1)
      }
    }
    val userStorage = mockk<UserStorage>(relaxed = true) {
      every { getCredential() } answers {
        mockk {
          every { isValid() } answers { true }
        }
      }
    }
    val commentRepository = CommentRepository(remarkService, userStorage)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.UP)
      result.getOrNull()!!.comments.first().comment.score shouldBe 1
      result.getOrNull()!!.comments.first().comment.vote shouldBe 1
    }
  }

  @Test
  fun `Verify change down vote`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockRootCommentTree(commentId, score = 6, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.DOWN.backendCode) } coAnswers {
        VoteResponse(commentId, 5)
      }
    }
    val userStorage = mockk<UserStorage>(relaxed = true) {
      every { getCredential() } answers {
        mockk {
          every { isValid() } answers { true }
        }
      }
    }
    val commentRepository = CommentRepository(remarkService, userStorage)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.DOWN)
      result.getOrNull()!!.comments.first().comment.score shouldBe 5
      result.getOrNull()!!.comments.first().comment.vote shouldBe -1
    }
  }

  @Test
  fun `Verify change down vote second level`() {
    val postUrl = "postUrl"
    val commentId = "comment_id"
    val remarkService = mockk<RemarkService> {
      coEvery { getComments(postUrl) } coAnswers {
        mockSecondLevelCommentTree(commentId, score = 2, vote = 0)
      }
      coEvery { vote(commentId, postUrl, VoteType.DOWN.backendCode) } coAnswers {
        VoteResponse(commentId, 1)
      }
    }
    val userStorage = mockk<UserStorage>(relaxed = true) {
      every { getCredential() } answers {
        mockk {
          every { isValid() } answers { true }
        }
      }
    }
    val commentRepository = CommentRepository(remarkService, userStorage)
    runBlocking {
      commentRepository.getComments(postUrl)
      val result = commentRepository.vote(commentId, postUrl, VoteType.DOWN)
      result.getOrNull()!!.comments.first().replies.first().comment.score shouldBe 1
      result.getOrNull()!!.comments.first().replies.first().comment.vote shouldBe -1
    }
  }

  @Test
  fun `Verify without cache`() {
    val remarkService = mockk<RemarkService>(relaxed = true)
    val userStorage = mockk<UserStorage>(relaxed = true)

    val commentRepository = CommentRepository(remarkService, userStorage)

    runBlocking {
      val result = commentRepository.vote("", "", VoteType.DOWN)
      result.isFailure shouldBe true
    }
  }

  @Test
  fun `Verify not auth user try vote`() {
    val remarkService = mockk<RemarkService> {
      coEvery { getComments(any()) } coAnswers {
        mockRootCommentTree("commentId", score = 6, vote = 0)
      }
    }
    val userStorage = mockk<UserStorage> {
      every { getCredential() } answers {
        mockk {
          every { isValid() } answers { false }
        }
      }
    }

    val commentRepository = CommentRepository(remarkService, userStorage)

    runBlocking {
      commentRepository.getComments("")
      val result = commentRepository.vote("", "", VoteType.DOWN)
      (result.exceptionOrNull() is NotAuthUser) shouldBe true
    }
  }

  @Test
  fun `Verify correct handle 401`() {
    val remarkService = mockk<RemarkService> {
      coEvery { getComments(any()) } coAnswers {
        mockRootCommentTree("commentId", score = 6, vote = 0)
      }
      coEvery { vote(any(), any(), any()) } throws HttpException(mockk(relaxed = true) {
        every { code() } answers { 401 }
      })
    }
    val userStorage = mockk<UserStorage> {
      every { getCredential() } answers {
        mockk {
          every { isValid() } answers { true }
        }
      }
    }

    val commentRepository = CommentRepository(remarkService, userStorage)

    runBlocking {
      commentRepository.getComments("")
      val result = commentRepository.vote("", "", VoteType.DOWN)
      (result.exceptionOrNull() is NotAuthUser) shouldBe true
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
