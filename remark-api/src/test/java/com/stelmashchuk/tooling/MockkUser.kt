package com.stelmashchuk.tooling

import com.stelmashchuk.remark.api.user.User
import com.stelmashchuk.remark.api.user.UserRepository
import io.mockk.every
import io.mockk.mockk

internal fun getUserRepository(mockUser: User?): UserRepository {
  return mockk {
    every { user } answers { mockUser }
  }
}
