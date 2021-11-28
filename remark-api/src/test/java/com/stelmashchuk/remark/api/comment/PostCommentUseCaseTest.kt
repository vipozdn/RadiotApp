package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.Locator
import com.stelmashchuk.remark.api.pojo.PostComment
import com.stelmashchuk.remark.api.repositories.FullComment
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

    val rootId = "rootId"
    val postUrl = "postUrl"

    val newText = "newText"

    val siteId = "siteId"
    val newId = "newId"

    val remarkService = mockk<CommentService> {
      coEvery { postComment(PostComment(newText, rootId, Locator(siteId, postUrl))) } answers {
        Comment(
            id = newId,
            parentId = rootId,
            text = newText,
            score = 0L,
            user = mockk(),
            time = "",
            vote = 0,
        )
      }
    }

    commentStorage.add(FullComment(rootId, "", "text", 0L, mockk(), LocalDateTime.MAX, 0, 0, true))

    val userCase = PostCommentUseCase(commentStorage, remarkService, CommentMapper(mockk(relaxed = true)))
    userCase.postComment(CommentRoot.Comment(postUrl, rootId), newText, postUrl, siteId)

    commentStorage.waitForComment(newId) should idMatch(newId).and(textMatch(newText)).and(replyCountMatch(0))
    commentStorage.waitForComment(rootId) should idMatch(rootId).and(replyCountMatch(1))
  }

}