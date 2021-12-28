package com.stelmashchuk.remark.os

import java.time.LocalDateTime
import java.time.ZoneOffset

internal class OsDateTime {

  fun nowUTC(): LocalDateTime {
    return LocalDateTime.now(ZoneOffset.UTC)
  }

}
