package com.example.remark.ui.comments.mappers

class TimeMapper {

  fun map(time: String): String {
    return time.split(".")[0]
  }

}
