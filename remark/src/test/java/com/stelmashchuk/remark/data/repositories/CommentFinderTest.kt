package com.stelmashchuk.remark.data.repositories

import com.stelmashchuk.remark.api.pojo.CommentWrapper
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class CommentFinderTest {

  @Test
  fun checkFind() {
    val commentFinder = com.stelmashchuk.remark.api.repositories.CommentFinder()

    val foundComment = listOf<CommentWrapper>(mockk(), mockk())

    commentFinder.getChildComments(
        listOf(CommentWrapper(
            comment = mockk {
              every { id } answers { "1" }
            },
            replies = listOf(
                CommentWrapper(
                    comment = mockk {
                      every { id } answers { "2" }
                    },
                    replies = listOf(
                        mockk(relaxed = true),
                        mockk(relaxed = true),
                    )
                ),
                CommentWrapper(
                    comment = mockk {
                      every { id } answers { "3" }
                    },
                    replies = foundComment
                ),
                CommentWrapper(
                    comment = mockk {
                      every { id } answers { "4" }
                    },
                    replies = listOf(
                        mockk(relaxed = true),
                        mockk(relaxed = true),
                    )
                ),
            )
        )), rootId = "3"
    ) shouldBe foundComment
  }

}