package com.stelmashchuk.remark.api.config

import com.stelmashchuk.remark.api.comment.CommentService

public class ConfigRepository internal constructor(private val commentService: CommentService) {

  private var config: Config? = null

  public suspend fun getConfig(): Config {
    return if (config != null) {
      config!!
    } else {
      commentService.getConfig().also {
        config = it
      }
    }
  }

}