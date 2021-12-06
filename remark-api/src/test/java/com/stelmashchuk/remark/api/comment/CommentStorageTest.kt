package com.stelmashchuk.remark.api.comment

import app.cash.turbine.test
import io.kotlintest.matchers.collections.shouldContain
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class CommentStorageTest {

  @Test
  fun `Verify several write operation`() = runBlocking {
    val storage = CommentStorage()

    val stableCommentId = "stableCommentId"
    val stableComment = createFullComment(stableCommentId)

    val commentToChangeId = "commentToChangeId"
    val commentToChange = createFullComment(commentToChangeId, 0)

    val newCommentId = "newCommentId"
    val newComment = createFullComment(newCommentId)

    storage.add(stableComment)
    storage.add(commentToChange)

    storage.observableComment(CommentRoot.Post(""))
        .test {
          awaitItem().run {
            this shouldContain stableComment
            this shouldContain commentToChange
          }

          val comment = storage.waitForComment(commentToChangeId)

          storage.transaction {
            replace(comment.id, comment.copy(replyCount = comment.replyCount.inc()))
            add(newComment)
          }

          awaitItem().run {
            this shouldContain stableComment
            this shouldContain commentToChange.copy(replyCount = 1)
            this shouldContain newComment
          }
        }
  }

  private fun createFullComment(id: String, replyCount: Int = 0): FullComment {
    return FullComment(
        id = id,
        parentId = "",
        text = "",
        score = 0L,
        user = mockk(),
        time = LocalDateTime.MAX,
        vote = 0,
        replyCount = replyCount,
        isCurrentUserAuthor = true,
    )
  }

}