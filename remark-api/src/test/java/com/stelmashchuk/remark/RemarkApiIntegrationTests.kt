package com.stelmashchuk.remark

import app.cash.turbine.test
import com.stelmashchuk.remark.api.RemarkApiFactory
import com.stelmashchuk.remark.api.comment.Comment
import com.stelmashchuk.remark.api.comment.CommentId
import com.stelmashchuk.remark.api.comment.CommentOneLevelRoot
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.api.comment.CommentService
import com.stelmashchuk.remark.api.comment.DeletedComment
import com.stelmashchuk.remark.api.comment.EditCommentRequest
import com.stelmashchuk.remark.api.comment.Locator
import com.stelmashchuk.remark.api.comment.PostComment
import com.stelmashchuk.remark.api.comment.VoteResponse
import com.stelmashchuk.remark.api.comment.VoteType
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class RemarkApiIntegrationTests {

  private val siteId = "site-id"

  @Test
  fun `Verify return just root without reply and root with reply`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = CommentId("rootCommentId")
    val rootComment = mockComment(rootCommentId, CommentId(""))
    val newCommentId = CommentId("newCommentId")
    val newText = "newText"
    val newComment = mockComment(newCommentId, rootCommentId, text = newText)

    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment)
        )
      }

      coEvery { postComment(PostComment(text = newText, parentId = rootCommentId, Locator(siteId, postUrl))) } coAnswers {
        newComment
      }
    }

    val useCases = createUseCases(service)
    val dataController = useCases.getDataController(postUrl)
    val postUseCases = useCases.getPostCommentUseCase(postUrl)

    val commentRoot = CommentRoot.Comment(postUrl, rootCommentId)

    dataController.observeComments(commentRoot)
        .test {
          awaitItem().run {
            this.rootComment should idMatch(rootCommentId).and(replyCountMatch(0))
            this.comments shouldBe emptyList()
          }

          postUseCases.postComment(commentRoot, newText) shouldBe null

          awaitItem().run {
            this.rootComment should idMatch(rootCommentId).and(replyCountMatch(1))
            this.comments[0] should idMatch(newCommentId).and(replyCountMatch(0)).and(textMatch(newText))
          }
        }
  }


  @Test
  fun `Verify delete 1th level comment`() = runBlocking {
    val postUrl = "postUrl"
    val commentToDelete = CommentId("commentToDelete")
    val comment1 = mockComment(commentToDelete, CommentId(""))
    val comment2 = mockComment(CommentId("1"), CommentId(""))

    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(comment1, comment2)
        )
      }

      coEvery { edit(commentToDelete.raw, EditCommentRequest(true), postUrl) } coAnswers {
        DeletedComment(id = commentToDelete)
      }
    }

    val useCases = createUseCases(service)
    val deleteCommentUseCases = useCases.getDeleteCommentUseCase(postUrl)
    val dataController = useCases.getDataController(postUrl)

    val root = CommentRoot.Post(postUrl)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            rootComment shouldBe null
            comments.any { it.id == commentToDelete } shouldBe true
            comments.any { it.id == CommentId("1") } shouldBe true
          }

          deleteCommentUseCases.delete(commentToDelete) shouldBe Result.success(Unit)

          awaitItem().run {
            comments[0] should idMatch("1")
          }
        }
  }

  @Test
  fun `Verify add 1th level comments`() = runBlocking {
    val postUrl = "postUrl"

    val oldComment = mockComment(CommentId("1"), CommentId(""))
    val newText = "newText"
    val newComment = mockComment(CommentId("2"), CommentId(""), text = newText)

    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(oldComment)
        )
      }

      coEvery { postComment(PostComment(newText, locator = Locator(siteId, postUrl))) } coAnswers {
        newComment
      }
    }

    val useCases = createUseCases(service)
    val dataController = useCases.getDataController(postUrl)
    val postUseCases = useCases.getPostCommentUseCase(postUrl)

    val root = CommentRoot.Post(postUrl)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            rootComment shouldBe null
            comments[0] should idMatch(oldComment.id)
          }

          postUseCases.postComment(root, newText) shouldBe null

          awaitItem().run {
            rootComment shouldBe null
            comments.find { it.id == newComment.id }?.text shouldBe newComment.text
          }
        }
  }

  @Test
  fun `Verify add 2th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = CommentId("rootCommentId")
    val rootComment = mockComment(rootCommentId, CommentId(""))
    val comment1 = mockComment(CommentId("1"), rootCommentId)

    val newText = "newText"
    val newComment = mockComment(CommentId("2"), rootCommentId, text = newText)

    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1)
        )
      }

      coEvery { postComment(PostComment(newText, rootCommentId, Locator(siteId, postUrl))) } coAnswers {
        newComment
      }
    }

    val useCases = createUseCases(service)
    val dataController = useCases.getDataController(postUrl)
    val postUseCases = useCases.getPostCommentUseCase(postUrl)

    val root = CommentRoot.Comment(postUrl, rootCommentId)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 1

            this.comments[0] should idMatch("1")
          }

          postUseCases.postComment(root, newText) shouldBe null

          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 2

            this.comments[0] should idMatch("1").and(replyCountMatch(0))
            this.comments[1] should idMatch("2").and(replyCountMatch(0)).and(textMatch(newText))
          }
        }
  }

  @Test
  fun `Verify get 1th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val postComment1 = mockComment(CommentId("1"), CommentId(""))
    val postComment2 = mockComment(CommentId("2"), CommentId(""))
    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(postComment1, mockComment(CommentId("3"), CommentId("2")), postComment2)
        )
      }
    }

    createUseCases(service).getDataController(postUrl)
        .observeComments(CommentRoot.Post(postUrl))
        .test {
          awaitItem().run {
            comments[0] should idMatch("1")
            comments[1] should idMatch("2")
          }
        }
  }

  @Test
  fun `Verify get 2th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = CommentId("aa-aa")
    val rootComment = mockComment(rootCommentId, CommentId(""))
    val comment1 = mockComment(CommentId("1"), rootCommentId)
    val comment2 = mockComment(CommentId("2"), rootCommentId)
    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1, comment2)
        )
      }
    }

    createUseCases(service).getDataController(postUrl)
        .observeComments(CommentRoot.Comment(postUrl, rootCommentId))
        .test {
          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 2

            this.comments[0] should idMatch("1")
            this.comments[1] should idMatch("2")
          }
        }
  }


  @Test
  fun `Verify vote apply for 2th level comment`(): Unit = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = CommentId("aa-aa")
    val voteCommentId = CommentId("1")
    val rootComment = mockComment(rootCommentId, CommentId(""))
    val comment1 = mockComment(voteCommentId, rootCommentId, 1, 0)
    val comment2 = mockComment(CommentId("2"), rootCommentId)
    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1, comment2)
        )
      }
      coEvery { vote(voteCommentId, postUrl, VoteType.UP.backendCode) } coAnswers {
        VoteResponse(voteCommentId, 2)
      }
      coEvery { vote(voteCommentId, postUrl, VoteType.DOWN.backendCode) } coAnswers {
        VoteResponse(voteCommentId, 1)
      }
    }

    val dataController = createUseCases(service).getDataController(postUrl)

    dataController.observeComments(CommentRoot.Comment(postUrl, rootCommentId))
        .test {
          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 2

            this.comments[0] should idMatch(voteCommentId).and(replyCountMatch(0)).and(scoreMatch(1)).and(voteMatch(0))
            this.comments[1] should idMatch("2")
          }

          dataController.vote(voteCommentId, VoteType.UP)

          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 2

            this.comments[0] should idMatch(voteCommentId).and(replyCountMatch(0)).and(scoreMatch(2)).and(voteMatch(1))
            this.comments[1] should idMatch("2")
          }

          dataController.vote(voteCommentId, VoteType.DOWN)

          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 2

            this.comments[0] should idMatch(voteCommentId).and(replyCountMatch(0)).and(scoreMatch(1)).and(voteMatch(-1))
            this.comments[1] should idMatch("2")
          }
        }
  }

  private fun createUseCases(service: CommentService): RemarkApiFactory {
    return RemarkApiFactory(
        siteId,
        mockk(relaxed = true),
        service,
        mockk(relaxed = true)
    )
  }

  private fun mockComment(
      mockId: CommentId, mockParentId: CommentId, mockScore: Int = 0, mockVote: Int = 0, text: String = "",
  ): Comment {
    return Comment(
        id = mockId,
        parentId = mockParentId,
        text = text,
        score = mockScore.toLong(),
        vote = mockVote,
        user = mockk(relaxed = true),
        time = ""
    )
  }
}
