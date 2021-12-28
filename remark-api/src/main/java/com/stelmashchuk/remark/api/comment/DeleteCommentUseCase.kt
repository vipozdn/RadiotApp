package com.stelmashchuk.remark.api.comment

public class DeleteCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val postUrl: String,
) {

  public suspend fun delete(commentId: CommentId): Result<Unit> {
    return Result.runCatching { commentService.edit(commentId.raw, EditCommentRequest(true), postUrl) }
        .onSuccess { deleteComment ->
          commentStorage.remove(deleteComment.id)
        }
        .map {}
  }
}