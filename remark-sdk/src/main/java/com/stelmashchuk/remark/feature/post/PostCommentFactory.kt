package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.RemarkApiFactory
import com.stelmashchuk.remark.api.comment.CommentRoot
import java.util.concurrent.ConcurrentHashMap

internal class PostCommentFactory(private val remarkApiFactory: RemarkApiFactory) {

  private val storageMap = ConcurrentHashMap<String, PostCommentStorage>()
  private val editModeMap = ConcurrentHashMap<String, EditMode>()

  fun createPostCommentViewModel(commentRoot: CommentRoot): PostCommentViewModel {
    val postComment = PostComment(
        postCommentUseCase = remarkApiFactory.getPostCommentUseCase(commentRoot.postUrl),
        postCommentStorage = getStorage(commentRoot.postUrl),
        editCommentUseCase = remarkApiFactory.getEditCommentUseCase(commentRoot.postUrl),
        editMode = getEditMode(commentRoot.postUrl),
        commentRoot = commentRoot,
    )
    return PostCommentViewModel(postComment = postComment, getStorage(commentRoot.postUrl))
  }

  internal fun getEditMode(postUrl: String): EditMode {
    return editModeMap.getOrPut(postUrl) {
      EditMode(commentStorage = remarkApiFactory.getStorage(postUrl), postCommentStorage = getStorage(postUrl))
    }
  }

  private fun getStorage(postUrl: String): PostCommentStorage {
    return storageMap.getOrPut(postUrl) {
      PostCommentStorage()
    }
  }
}
