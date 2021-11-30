package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.config.EditCommentRequest
import kotlinx.coroutines.flow.Flow

class DeleteCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val postUrl: String,
) {

  fun observeCommentDeleteAvailable(commentId: String): Flow<FullComment?> {
    return commentStorage.observableComment(commentId)
  }

  suspend fun delete(commentId: String): Result<Unit> {
    return Result.runCatching { commentService.edit(commentId, EditCommentRequest(true), postUrl) }
        .onSuccess { deleteComment ->
          commentStorage.remove(deleteComment.id)
        }
        .map {}
  }
}