package com.example.remark.feature.comments.mappers

class TimeMapper {

  fun map(time: String): String {
    return time.split(".")[0]
  }

}
