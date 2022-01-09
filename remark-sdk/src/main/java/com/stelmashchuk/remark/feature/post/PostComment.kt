package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.api.comment.EditCommentUseCase
import com.stelmashchuk.remark.api.comment.PostCommentUseCase

internal class PostComment(
    private val postCommentUseCase: PostCommentUseCase,
    private val postCommentStorage: PostCommentStorage,
    private val editCommentUseCase: EditCommentUseCase,
    private val editMode: EditMode,
    private val commentRoot: CommentRoot,
) {

  internal suspend fun postComment() {
    if (editMode.flowEditCommentId().value != null) {
      editComment()
    } else {
      postNewComment()
    }
  }

  private suspend fun postNewComment() {
    val text = postCommentStorage.flowText().value
    val result = postCommentUseCase.postComment(commentRoot, text)
    if (result.isSuccess) {
      postCommentStorage.clear()
    }
  }

  private suspend fun editComment() {
    val id = requireNotNull(editMode.flowEditCommentId().value)
    val text = postCommentStorage.flowText().value

    if (editCommentUseCase.editComment(id, text).isSuccess) {
      postCommentStorage.clear()
      editMode.closeEditMode()
    }
  }
}
