package com.stelmashchuk.remark.data.repositories

import com.stelmashchuk.remark.api.repositories.CredentialCreator
import com.stelmashchuk.remark.api.repositories.RemarkCredentials
import io.kotlintest.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class CredentialCreatorTest {

  val goodJWT = "value"
  val xsrfToken = "token"
  val goodString = "JWT=$goodJWT; XSRF-TOKEN=$xsrfToken"

  private val credentialCreator = CredentialCreator()

  @TestFactory
  fun tryCreate() = listOf(
      "" to null,
      goodString to RemarkCredentials(goodJWT, xsrfToken),
      "XSRF-TOKEN=$xsrfToken" to null,
      "JWT=$goodJWT; XSRF-TOKEN=$xsrfToken; another=another" to RemarkCredentials(
          goodJWT,
          xsrfToken
      ),
      "JWT=$goodJWT; XSRF" to null
  ).map {
    DynamicTest.dynamicTest("Try parse cookies ${it.first}") {
      val input = it.first
      val expected = it.second

      credentialCreator.tryCreate(input) shouldBe expected
    }
  }
}
