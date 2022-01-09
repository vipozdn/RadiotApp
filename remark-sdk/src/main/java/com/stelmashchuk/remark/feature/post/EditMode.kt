package com.stelmashchuk.remark.feature.post

import com.stelmashchuk.remark.api.comment.CommentId

internal interface EditMode {

  suspend fun startEditMode(commentId: CommentId)

  suspend fun closeEditMode()

}
