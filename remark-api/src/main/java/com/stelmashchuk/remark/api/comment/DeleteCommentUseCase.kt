package com.stelmashchuk.remark.api.comment

import kotlinx.coroutines.flow.Flow

class DeleteCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
) {

  fun observeCommentDeleteAvailable(commentId: String): Flow<FullComment?> {
    return commentStorage.observableComment(commentId)
  }

  suspend fun delete(commentId: String): Any? {
    val deletedComment = commentService.delete(commentId)
    commentStorage.remove(deletedComment.id)
    return null
  }
}