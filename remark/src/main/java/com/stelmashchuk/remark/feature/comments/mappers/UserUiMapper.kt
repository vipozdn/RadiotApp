package com.stelmashchuk.remark.feature.comments.mappers

import com.stelmashchuk.remark.api.pojo.User
import com.stelmashchuk.remark.feature.comments.CommentAuthorUiModel

class UserUiMapper {
  fun map(user: com.stelmashchuk.remark.api.pojo.User): CommentAuthorUiModel {
    return CommentAuthorUiModel(
        name = user.name,
        avatar = user.avatar,
    )
  }
}
