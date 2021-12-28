package com.stelmashchuk.remark.api.comment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

public class CommentStorage {

  private val data = HashMap<CommentId, FullComment>()

  private val flow = MutableStateFlow(emptyList<FullComment>())

  private val editor = Editor(data)

  public fun hasData(): Boolean {
    return data.isNotEmpty()
  }

  public suspend fun waitForComment(id: CommentId): FullComment {
    return flow
        .map { comments ->
          comments.find { it.id == id }
        }
        .first { it != null }!!
  }

  public fun observableComment(commentRoot: CommentRoot): Flow<List<FullComment>> {
    return flow
        .map { comments ->
          comments.filter { checkIsCommented(commentRoot, it) }
        }
  }

  internal suspend fun transaction(block: suspend Editor.() -> Unit) {
    editor.block()
    reEmit()
  }

  internal suspend fun setup(comments: List<FullComment>) {
    data.putAll(comments.map { Pair(it.id, it) })
    reEmit()
  }

  internal suspend fun add(comment: FullComment) {
    editor.add(comment)
    reEmit()
  }

  internal suspend fun remove(id: CommentId) {
    editor.remove(id)
    reEmit()
  }

  internal suspend fun replace(commentId: CommentId, comment: FullComment) {
    editor.replace(commentId, comment)
    reEmit()
  }

  private suspend fun reEmit() {
    flow.emit(data.values.toList())
  }

  private fun checkIsCommented(commentRoot: CommentRoot, comment: FullComment): Boolean {
    return when (commentRoot) {
      is CommentRoot.Comment -> commentRoot.commentId == comment.parentId
      is CommentRoot.Post -> !comment.parentId.isValid()
    }
  }

  internal inner class Editor(private val data: HashMap<CommentId, FullComment>) {

    fun add(comment: FullComment) {
      data[comment.id] = comment
    }

    fun remove(id: CommentId) {
      data.remove(id)
    }

    fun replace(commentId: CommentId, comment: FullComment) {
      data[commentId] = comment
    }
  }
}
