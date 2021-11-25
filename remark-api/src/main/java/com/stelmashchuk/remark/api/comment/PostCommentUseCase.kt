package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.RemarkError
import com.stelmashchuk.remark.api.network.RemarkService
import com.stelmashchuk.remark.api.pojo.Comment
import com.stelmashchuk.remark.api.pojo.Locator
import com.stelmashchuk.remark.api.pojo.PostComment
import com.stelmashchuk.remark.api.repositories.FullComment

internal class PostCommentUseCase(
    private val commentStorage: CommentStorage,
    private val remarkService: RemarkService,
) {

  suspend fun postComment(
      commentRoot: CommentRoot,
      text: String,
      postUrl: String,
      siteId: String,
  ): RemarkError? {
    val commentResult = Result.runCatching {
      remarkService.postComment(PostComment(
          text = text,
          parentId = if (commentRoot is CommentRoot.Comment) commentRoot.commentId else null,
          locator = Locator(siteId, postUrl),
      ))
    }

    commentResult.getOrNull()?.let { comment ->
      commentStorage.transaction {
        add(mapOneCommentToFullComment(comment))

        val parentId = comment.parentId
        if (parentId.isNotBlank()) {
          val parent = commentStorage.waitForComment(parentId)
          replace(parentId, parent.copy(replyCount = parent.replyCount.inc()))
        }
      }
    }

    return null
  }

  private fun mapOneCommentToFullComment(comment: Comment): FullComment {
    return FullComment(
        id = comment.id,
        parentId = comment.parentId,
        text = comment.text,
        score = comment.score,
        user = comment.user,
        time = comment.time,
        vote = comment.vote,
        replyCount = 0,
        isCurrentUserAuthor = true
    )
  }
}
