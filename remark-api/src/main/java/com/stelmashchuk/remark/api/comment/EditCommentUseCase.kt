package com.stelmashchuk.remark.api.comment

public class EditCommentUseCase internal constructor(
    private val commentStorage: CommentStorage,
    private val commentService: CommentService,
    private val postUrl: String,
) {

  public suspend fun editComment(commentId: CommentId, text: String): Result<Unit> {
    return Result.runCatching {
      commentService.edit(
          commentId = commentId,
          editRequest = EditRequest(text = text),
          postUrl = postUrl,
      )
    }
        .onSuccess { comment ->
          val original = commentStorage.waitForComment(comment.id)
          commentStorage.replace(original.id, original.copy(text = comment.text))
        }
        .map { }
  }
}
