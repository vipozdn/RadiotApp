package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.CommentRoot
import com.stelmashchuk.remark.api.repositories.FullComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class CommentStorage {

  private val flow = MutableStateFlow(emptyList<FullComment>())

  suspend fun hasData(): Boolean {
    return flow.value.isNotEmpty()
  }

  suspend fun setup(comments: List<FullComment>) {
    flow.emit(comments)
  }

  suspend fun add(comment: FullComment) {
    flow.emit(flow.value.plus(comment))
  }

  suspend fun remote(id: String) {
    flow.emit(flow.value.filter { comment -> comment.id != id })
  }

  suspend fun replace(map : Map<String, FullComment>) {

  }

  suspend fun replace(commentId: String, comment: FullComment) {
    val comments = flow.value.toMutableList()
    comments.replaceAll {
      if (it.id == commentId) {
        comment
      } else {
        it
      }
    }
    flow.emit(comments.toList())
  }

  suspend fun waitForComment(id: String): FullComment {
    return flow
        .map { comments ->
          comments.find { it.id == id }
        }
        .first { it != null }!!
  }

  fun observableComment(commentRoot: CommentRoot): Flow<List<FullComment>> {
    return flow
        .map { comments ->
          comments.filter { checkIsCommented(commentRoot, it) }
        }
  }

  private fun checkIsCommented(commentRoot: CommentRoot, comment: FullComment): Boolean {
    return when (commentRoot) {
      is CommentRoot.Comment -> commentRoot.commentId == comment.parentId
      is CommentRoot.Post -> comment.parentId.isBlank()
    }
  }
}
