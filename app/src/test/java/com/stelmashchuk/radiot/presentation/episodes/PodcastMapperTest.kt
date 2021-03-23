package com.stelmashchuk.radiot.presentation.episodes

import com.stelmashchuk.radiot.data.Podcast
import com.stelmashchuk.radiot.data.TimeLabels
import com.stelmashchuk.radiot.presentation.episodes.EpisodeUiModel
import com.stelmashchuk.radiot.presentation.episodes.PodcastMapper
import org.junit.Assert
import org.junit.Test


internal class PodcastMapperTest {

  @Test
  fun `Verify mapping to ui model`() {
    val title = "title"
    val number = 4L
    val podcast = Podcast(
        "",
        title,
        number,
        listOf(
            TimeLabels("t1"),
            TimeLabels("t2"),
            TimeLabels("t3"),
        ),
    )

    Assert.assertEquals(
        EpisodeUiModel(
            number,
            title,
            "- t1\n- t2\n- t3",
            "",
        ),
        PodcastMapper().map(podcast)
    )
  }
}
