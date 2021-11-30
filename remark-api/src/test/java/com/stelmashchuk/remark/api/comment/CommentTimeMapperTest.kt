package com.stelmashchuk.remark.api.comment

import io.kotlintest.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.LocalDateTime


internal class CommentTimeMapperTest {

  //2021-11-30T13:57:23.308974867-06:00

  private val timeMapper = CommentTimeMapper()

  @TestFactory
  fun `verify correct time parsing`(): List<DynamicTest> {
    return listOf(
        "2021-11-30T13:57:23.308974867-06:00" to LocalDateTime.of(2021, 11, 30, 19, 57, 23),
        "2021-11-30T13:57:23.308974867-05:00" to LocalDateTime.of(2021, 11, 30, 18, 57, 23),
        "2021-11-30T13:57:23.308974867-00:00" to LocalDateTime.of(2021, 11, 30, 13, 57, 23),
    )
        .map { (be, expected) ->
          DynamicTest.dynamicTest("Check correct time parsing") {
            timeMapper.map(be) shouldBe expected
          }
        }

  }
}