package com.stelmashchuk.remark.api.comment

class DeleteCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val postUrl: String,
) {

  suspend fun getCommentById(commentId: String): FullComment {
    return commentStorage.waitForComment(commentId)
  }

  suspend fun delete(commentId: String): Result<Unit> {
    return Result.runCatching { commentService.edit(commentId, EditCommentRequest(true), postUrl) }
        .onSuccess { deleteComment ->
          commentStorage.remove(deleteComment.id)
        }
        .map {}
  }
}