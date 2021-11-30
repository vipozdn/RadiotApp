package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.config.Locator
import com.stelmashchuk.remark.api.config.PostComment

public class PostCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val commentMapper: CommentMapper,
    private val siteId: String,
    private val postUrl: String,
) {

  suspend fun postComment(
      commentRoot: CommentRoot,
      text: String,
  ): RemarkError? {
    val result = Result.runCatching {
      commentService.postComment(PostComment(
          text = text,
          parentId = if (commentRoot is CommentRoot.Comment) commentRoot.commentId else null,
          locator = Locator(siteId, postUrl),
      ))
    }.onSuccess { comment ->
      commentStorage.transaction {
        add(commentMapper.map(comment))

        val parentId = comment.parentId
        if (parentId.isNotBlank()) {
          val parent = commentStorage.waitForComment(parentId)
          replace(parentId, parent.copy(replyCount = parent.replyCount.inc()))
        }
      }
    }

    return if (result.isSuccess) null else throw NotImplementedError()
  }
}
