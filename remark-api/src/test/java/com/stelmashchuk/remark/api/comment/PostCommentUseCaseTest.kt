package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.idMatch
import com.stelmashchuk.remark.replyCountMatch
import com.stelmashchuk.remark.textMatch
import io.kotlintest.should
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class PostCommentUseCaseTest {

  @Test
  fun `Verify post 2th level comment`() = runBlocking {
    val commentStorage = CommentStorage()

    val rootId = CommentId("rootId")
    val postUrl = "postUrl"

    val newText = "newText"

    val siteId = "siteId"
    val newId = CommentId("newId")

    val remarkService = mockk<CommentService> {
      coEvery { postComment(PostComment(newText, rootId, Locator(postUrl, siteId))) } coAnswers {
        Comment(
            id = newId,
            parentId = rootId,
            text = newText,
            score = 0L,
            user = mockk(relaxed = true),
            time = "2021-11-30T13:57:23.308974867-06:00",
            vote = 0,
        )
      }
    }

    commentStorage.add(FullComment(rootId, CommentId(""), "text", 0L, mockk(), LocalDateTime.MAX, 0, 0, true))

    val userCase = PostCommentUseCase(commentStorage, remarkService, CommentMapper(mockk(relaxed = true), mockk(relaxed = true)), postUrl, siteId)
    userCase.postComment(CommentRoot.Comment(postUrl, rootId), newText)

    commentStorage.waitForComment(newId) should idMatch(newId).and(textMatch(newText)).and(replyCountMatch(0))
    commentStorage.waitForComment(rootId) should idMatch(rootId).and(replyCountMatch(1))
  }

}