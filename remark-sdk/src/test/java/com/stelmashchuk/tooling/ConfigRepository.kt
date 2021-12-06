package com.stelmashchuk.tooling

import com.stelmashchuk.remark.api.config.ConfigRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

internal fun getConfigRepository(mockEditDuration: Long): ConfigRepository {
  return mockk(relaxed = true) {
    coEvery { getConfig() } coAnswers {
      mockk {
        every { editDuration } answers { mockEditDuration }
      }
    }
  }
}