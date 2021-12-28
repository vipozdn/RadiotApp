package com.stelmashchuk.remark.api.comment

import com.stelmashchuk.remark.api.RemarkApi
import io.kotlintest.shouldBe
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class CommentTest {

  @Language("json")
  private val commentJson = """{"id":"ab071bd8-da45-460d-95ac-ba25a084e605","pid":"","text":"<p>333333</p>\n","orig":"333333","user":{"name":"Volodymyr","id":"github_c9a8e6cecd78f6d0c5aee29d13e5f6109b40fa99","picture":"https://demo.remark42.com/api/v1/avatar/8e9475a5770f94e080cf942224788dbbffa60dc0.image","admin":false,"site_id":"remark"},"locator":{"site":"remark","url":"https://remark42.com/demo/"},"score":0,"vote":0,"time":"2021-12-28T07:32:19.70225758-06:00","title":"Demo | Remark42"}"""


  @Test
  fun `Verify comment parsing correctly`() {
    val remarkApi = RemarkApi(mockk(relaxed = true), mockk(relaxed = true))

    remarkApi.json.decodeFromString<Comment>(commentJson).id shouldBe CommentId("ab071bd8-da45-460d-95ac-ba25a084e605")
  }

}