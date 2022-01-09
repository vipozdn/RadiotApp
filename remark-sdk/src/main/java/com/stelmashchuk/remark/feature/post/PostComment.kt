package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.comment.PostCommentUseCase

internal class PostComment(
    private val postCommentUseCase: PostCommentUseCase,
    private val postCommentStorage: PostCommentStorage,
) : EditMode by postCommentStorage {

  internal suspend fun postComment() {
    if (postCommentStorage.flowEditCommentId().value != null) {
      editComment()
    } else {
      postNewComment()
    }
  }

  private suspend fun postNewComment() {
    val root = postCommentStorage.root
    val text = postCommentStorage.flowText().value
    val error = postCommentUseCase.postComment(root, text)
    if (error == null) {
      postCommentStorage.clear()
    }
  }

  private suspend fun editComment() {
    throw NotImplementedError()
  }
}
