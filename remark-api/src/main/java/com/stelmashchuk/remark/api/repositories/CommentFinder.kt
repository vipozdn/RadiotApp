package com.stelmashchuk.remark.api.repositories

import com.stelmashchuk.remark.api.pojo.CommentWrapper

class CommentFinder {

  fun getChildComments(comments: List<CommentWrapper>, rootId: String): List<CommentWrapper> {
    comments.forEach {
      val replay = findCommentReplays(it, rootId)
      if (replay.isNotEmpty()) {
        return replay
      }
    }
    throw IllegalArgumentException("Replays not found")
  }

  private fun findCommentReplays(commentWrapper: CommentWrapper, rootId: String): List<CommentWrapper> {
    return if (commentWrapper.comment.id == rootId) {
      return commentWrapper.replies
    } else {
      commentWrapper.replies.find { findCommentReplays(it, rootId).isNotEmpty() }?.replies.orEmpty()
    }
  }
}