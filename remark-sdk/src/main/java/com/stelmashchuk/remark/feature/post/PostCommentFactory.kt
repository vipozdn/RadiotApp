package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.RemarkApiFactory
import com.stelmashchuk.remark.api.comment.CommentRoot
import java.util.concurrent.ConcurrentHashMap

internal class PostCommentFactory(private val remarkApiFactory: RemarkApiFactory) {

  private val storageMap = ConcurrentHashMap<CommentRoot, PostCommentStorage>()

  fun create(commentRoot: CommentRoot): PostCommentViewModel {
    val postComment = PostComment(
        postCommentUseCase = remarkApiFactory.getPostCommentUseCase(commentRoot.postUrl),
        postCommentStorage = getStorage(commentRoot)
    )
    return PostCommentViewModel(postComment = postComment, getStorage(commentRoot))
  }

  private fun getStorage(commentRoot: CommentRoot): PostCommentStorage {
    return storageMap.getOrPut(commentRoot) {
      PostCommentStorage(commentRoot)
    }
  }
}
