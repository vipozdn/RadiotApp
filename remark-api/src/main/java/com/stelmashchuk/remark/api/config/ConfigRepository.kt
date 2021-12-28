package com.stelmashchuk.remark.api.config

import com.stelmashchuk.remark.api.comment.CommentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

public class ConfigRepository internal constructor(private val commentService: CommentService) {

  public suspend fun getConfig(): Config {
    return Once(commentService::getConfig).invoke()
  }
}

internal class Once<T>(block: suspend () -> T) {
  private val scope = CoroutineScope(Dispatchers.Default)
  private val cache = scope.async(start = CoroutineStart.LAZY) {
    block()
  }

  suspend operator fun invoke() = cache.await()
}