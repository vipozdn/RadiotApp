package com.stelmashchuk.remark.feature.post.integration

import com.stelmashchuk.remark.api.RemarkApiFactory
import com.stelmashchuk.remark.api.comment.CommentRoot
import com.stelmashchuk.remark.api.comment.PostCommentUseCase
import com.stelmashchuk.remark.feature.post.PostCommentFactory
import com.stelmashchuk.remark.feature.post.PostCommentViewModel
import io.mockk.every
import io.mockk.mockk

internal fun createViewModel(
    postCommentUseCase: PostCommentUseCase = mockk(),
    commentRoot: CommentRoot = mockk(relaxed = true),
): PostCommentViewModel {
  val apiFactory = mockk<RemarkApiFactory> {
    every { getPostCommentUseCase(any()) } answers { postCommentUseCase }
  }
  val factory = PostCommentFactory(apiFactory)
  return factory.create(commentRoot = commentRoot)
}