package com.stelmashchuk.remark

import app.cash.turbine.test
import com.stelmashchuk.remark.api.CommentDataController
import com.stelmashchuk.remark.api.CommentDataControllerProvider
import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.comment.CommentService
import com.stelmashchuk.remark.api.config.Comment
import com.stelmashchuk.remark.api.config.CommentOneLevelRoot
import com.stelmashchuk.remark.api.config.DeletedComment
import com.stelmashchuk.remark.api.config.Locator
import com.stelmashchuk.remark.api.config.PostComment
import com.stelmashchuk.remark.api.config.VoteResponse
import com.stelmashchuk.remark.api.config.VoteType
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CommentDataControllerIntegrationTest {

  private val siteId = "site-id"

  @Test
  fun `Verify return just root without reply and root with reply`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = "rootCommentId"
    val rootComment = mockComment(rootCommentId, "")
    val newCommentId = "newCommentId"
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

    val dataController = createCommentDataController(postUrl, service)

    val commentRoot = CommentRoot.Comment(postUrl, rootCommentId)

    dataController.observeComments(commentRoot)
        .test {
          awaitItem().run {
            this.rootComment should idMatch(rootCommentId).and(replyCountMatch(0))
            this.comments shouldBe emptyList()
          }

          dataController.postComment(commentRoot, newText) shouldBe null

          awaitItem().run {
            this.rootComment should idMatch(rootCommentId).and(replyCountMatch(1))
            this.comments[0] should idMatch(newCommentId).and(replyCountMatch(0)).and(textMatch(newText))
          }
        }
  }


  @Test
  fun `Verify delete 1th level comment`() = runBlocking {
    val postUrl = "postUrl"
    val commentToDelete = "commentToDelete"
    val comment1 = mockComment(commentToDelete, "")
    val comment2 = mockComment("1", "")

    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(comment1, comment2)
        )
      }

      coEvery { delete(commentToDelete) } coAnswers {
        DeletedComment(id = commentToDelete)
      }
    }

    val dataController = createCommentDataController(postUrl, service)

    val root = CommentRoot.Post(postUrl)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            rootComment shouldBe null
            comments.any { it.id == commentToDelete } shouldBe true
            comments.any { it.id == "1" } shouldBe true
          }


          dataController.delete(commentToDelete) shouldBe null

          awaitItem().run {
            comments[0] should idMatch("1")
          }
        }
  }

  @Test
  fun `Verify add 1th level comments`() = runBlocking {
    val postUrl = "postUrl"

    val oldComment = mockComment("1", "")
    val newText = "newText"
    val newComment = mockComment("2", "", text = newText)

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

    val dataController = createCommentDataController(postUrl, service)

    val root = CommentRoot.Post(postUrl)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            rootComment shouldBe null
            comments[0] should idMatch(oldComment.id)
          }

          dataController.postComment(root, newText) shouldBe null

          awaitItem().run {
            rootComment shouldBe null
            comments.find { it.id == newComment.id }?.text shouldBe newComment.text
          }
        }
  }

  @Test
  fun `Verify add 2th level comments`() = runBlocking {
    val postUrl = "postUrl"
    val rootCommentId = "rootCommentId"
    val rootComment = mockComment(rootCommentId, "")
    val comment1 = mockComment("1", rootCommentId)

    val newText = "newText"
    val newComment = mockComment("2", rootCommentId, text = newText)

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

    val dataController = createCommentDataController(postUrl, service)

    val root = CommentRoot.Comment(postUrl, rootCommentId)

    dataController.observeComments(root)
        .test {
          awaitItem().run {
            this.rootComment?.id shouldBe rootCommentId
            this.rootComment?.replyCount shouldBe 1

            this.comments[0] should idMatch("1")
          }

          dataController.postComment(root, newText) shouldBe null

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
    val postComment1 = mockComment("1", "")
    val postComment2 = mockComment("2", "")
    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(postComment1, mockComment("3", "2"), postComment2)
        )
      }
    }

    createCommentDataController(postUrl, service)
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
    val rootCommentId = "aa-aa"
    val rootComment = mockComment(rootCommentId, "")
    val comment1 = mockComment("1", rootCommentId)
    val comment2 = mockComment("2", rootCommentId)
    val service = mockk<CommentService> {
      coEvery { getCommentsPlain(postUrl) } coAnswers {
        CommentOneLevelRoot(
            listOf(rootComment, comment1, comment2)
        )
      }
    }

    createCommentDataController(postUrl, service)
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
    val rootCommentId = "aa-aa"
    val voteCommentId = "1"
    val rootComment = mockComment(rootCommentId, "")
    val comment1 = mockComment(voteCommentId, rootCommentId, 1, 0)
    val comment2 = mockComment("2", rootCommentId)
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

    val dataController = createCommentDataController(postUrl, service)

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

  private fun createCommentDataController(postUrl: String, service: CommentService): CommentDataController {
    return CommentDataControllerProvider(service, siteId, mockk(relaxed = true)).getDataController(postUrl)
  }

  private fun mockComment(
      mockId: String, mockParentId: String, mockScore: Int = 0, mockVote: Int = 0, text: String = "",
  ): Comment {
    return Comment(
        id = mockId,
        parentId = mockParentId,
        text = text,
        score = mockScore.toLong(),
        vote = mockVote,
        user = mockk(),
        time = ""
    )
  }
}
