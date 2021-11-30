package com.stelmashchuk.remark.feature.delete

import com.stelmashchuk.remark.os.OsDateTime
import com.stelmashchuk.tooling.getConfigRepository
import com.stelmashchuk.tooling.mockFullComment
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class DeleteAvailableCheckerTest {

  @Test
  fun `verify is delete flag set correctly (1)`() = runBlocking {
    val someDate = LocalDate.of(2021, 1, 1)
    val commentTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 10))
    val systemTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 20))

    prepareData(systemTime, 300)
        .check(mockFullComment(true, commentTime)) shouldBe true
  }

  @Test
  fun `verify is delete flag set correctly (2)`() = runBlocking {
    val someDate = LocalDate.of(2021, 1, 1)
    val commentTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 10))
    val systemTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 11))

    prepareData(systemTime, 300)
        .check(mockFullComment(true, commentTime)) shouldBe true
  }

  @Test
  fun `verify is delete flag set correctly (3)`() = runBlocking {
    val someDate = LocalDate.of(2021, 1, 1)
    val commentTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 10))
    val systemTime = LocalDateTime.of(someDate, LocalTime.of(1, 2, 10))

    prepareData(systemTime, 60)
        .check(mockFullComment(true, commentTime)) shouldBe true
  }

  @Test
  fun `verify is delete flag set correctly (4)`() = runBlocking {
    val someDate = LocalDate.of(2021, 1, 1)
    val commentTime = LocalDateTime.of(someDate, LocalTime.of(1, 1, 10))
    val systemTime = LocalDateTime.of(someDate, LocalTime.of(1, 2, 11))

    prepareData(systemTime, 60)
        .check(mockFullComment(true, commentTime)) shouldBe false
  }

  @Test
  fun `verify is delete flag set correctly (year yes)`() = runBlocking {
    val commentTime = LocalDateTime.of(LocalDate.of(2021, 12, 31), LocalTime.of(23, 59, 10))
    val systemTime = LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.of(0, 0, 10))

    prepareData(systemTime, 60)
        .check(mockFullComment(true, commentTime)) shouldBe true
  }

  @Test
  fun `verify is delete flag set correctly (year no)`() = runBlocking {
    val commentTime = LocalDateTime.of(LocalDate.of(2021, 12, 31), LocalTime.of(23, 58, 10))
    val systemTime = LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.of(0, 1, 10))

    prepareData(systemTime, 60)
        .check(mockFullComment(true, commentTime)) shouldBe false
  }

  private fun prepareData(
      systemTime: LocalDateTime,
      editDuration: Long,
  ): DeleteAvailableChecker {
    val configRepository = getConfigRepository(editDuration)

    val osDateTime = mockk<OsDateTime> {
      every { nowUTC() } returns systemTime
    }

    return DeleteAvailableChecker(configRepository, osDateTime)
  }

  @Test
  fun `Verify correct for isCurrentAuthor false`() = runBlocking {
    DeleteAvailableChecker(mockk(relaxed = true), mockk(relaxed = true)).check(mockFullComment(false, mockk())) shouldBe false
  }

}
