package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.RemarkError
import com.stelmashchuk.remark.api.config.Locator
import com.stelmashchuk.remark.api.config.PostComment

internal class PostCommentUseCase(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val commentMapper: CommentMapper,
) {

  suspend fun postComment(
      commentRoot: CommentRoot,
      text: String,
      postUrl: String,
      siteId: String,
  ): RemarkError? {
    val commentResult = Result.runCatching {
      commentService.postComment(PostComment(
          text = text,
          parentId = if (commentRoot is CommentRoot.Comment) commentRoot.commentId else null,
          locator = Locator(siteId, postUrl),
      ))
    }

    commentResult.getOrNull()?.let { comment ->
      commentStorage.transaction {
        add(commentMapper.map(comment))

        val parentId = comment.parentId
        if (parentId.isNotBlank()) {
          val parent = commentStorage.waitForComment(parentId)
          replace(parentId, parent.copy(replyCount = parent.replyCount.inc()))
        }
      }
    }

    return null
  }
}
