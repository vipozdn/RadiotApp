package com.stelmashchuk.remark.api.new

import com.stelmashchuk.remark.api.RemarkService
import com.stelmashchuk.remark.api.pojo.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1

class CommentDataControllerProvider(private val remarkService: RemarkService) {

  private val map = HashMap<String, CommentDataController>()

  fun getDataController(postUrl: String, scope: CoroutineScope): CommentDataController {
    return map.getOrPut(postUrl) {
      CommentDataController(postUrl, scope, remarkService)
    }
  }

  fun clean(postUrl: String) {
    map.remove(postUrl)
  }
}

sealed class CommentRoot(open val postUrl: String) {
  data class Post(
      override val postUrl: String,
  ) : CommentRoot(postUrl)

  data class Comment(
      override val postUrl: String,
      val commentId: String,
  ) : CommentRoot(postUrl)
}

data class CommentInfo(
    val comment: Comment,
    val replayCount: Int,
)

class CommentDataController(
    private val postUrl: String,
    private val scope: CoroutineScope,
    private val remarkService: RemarkService,
) {

  private val flow = MutableStateFlow<List<Comment>>(emptyList())

  init {
    scope.launch {
      flow.tryEmit(remarkService.getCommentsPlain(postUrl).comments)
    }
  }

  private fun getReplayCount(commentId: String): Int {
    val comments = flow.value
    return comments.count { it.parentId == commentId }
  }

  fun observeComments(commentRoot: CommentRoot): Flow<List<CommentInfo>> {
    fun rootCommentFilter(comment: Comment): Boolean = comment.parentId.isBlank()
    fun notRootCommentFilter(comment: Comment): Boolean = comment.parentId == (commentRoot as CommentRoot.Comment).commentId

    val filterPrediction: KFunction1<Comment, Boolean> = when (commentRoot) {
      is CommentRoot.Comment -> {
        ::notRootCommentFilter
      }
      is CommentRoot.Post -> {
        ::rootCommentFilter
      }
    }
    return flow
        .map { comments ->
          return@map comments.filter(filterPrediction)
              .map {
                CommentInfo(it, getReplayCount(it.id))
              }
        }
  }
}
