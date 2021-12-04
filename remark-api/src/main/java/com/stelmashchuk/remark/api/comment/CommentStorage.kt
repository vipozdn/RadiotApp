package com.stelmashchuk.remark.api.comment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class CommentStorage {

  private val data = HashMap<String, FullComment>()

  private val flow = MutableStateFlow(emptyList<FullComment>())

  private val editor = Editor(data)

  suspend fun transaction(block: suspend Editor.() -> Unit) {
    editor.block()
    reEmit()
  }

  fun hasData(): Boolean {
    return data.isNotEmpty()
  }

  suspend fun setup(comments: List<FullComment>) {
    data.putAll(comments.map { Pair(it.id, it) })
    reEmit()
  }

  suspend fun add(comment: FullComment) {
    editor.add(comment)
    reEmit()
  }

  suspend fun remove(id: String) {
    editor.remove(id)
    reEmit()
  }

  suspend fun replace(commentId: String, comment: FullComment) {
    editor.replace(commentId, comment)
    reEmit()
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

  private suspend fun reEmit() {
    flow.emit(data.values.toList())
  }

  private fun checkIsCommented(commentRoot: CommentRoot, comment: FullComment): Boolean {
    return when (commentRoot) {
      is CommentRoot.Comment -> commentRoot.commentId == comment.parentId
      is CommentRoot.Post -> comment.parentId.isBlank()
      else -> throw IllegalArgumentException()
    }
  }

  inner class Editor(private val data: HashMap<String, FullComment>) {

    fun add(comment: FullComment) {
      data[comment.id] = comment
    }

    fun remove(id: String) {
      data.remove(id)
    }

    fun replace(commentId: String, comment: FullComment) {
      data[commentId] = comment
    }
  }
}
