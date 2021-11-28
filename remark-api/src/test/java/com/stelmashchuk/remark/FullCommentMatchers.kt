package com.stelmashchuk.remark

import com.stelmashchuk.remark.api.FullComment
import io.kotlintest.Matcher
import io.kotlintest.MatcherResult

fun idMatch(id: String): Matcher<FullComment?> {
  return object : Matcher<FullComment?> {
    override fun test(value: FullComment?): MatcherResult {
      return MatcherResult.invoke(value?.id == id, { "id should be $id but was ${value?.id}" }, { "id should not be $id but was ${value?.id}" })
    }
  }
}

fun textMatch(text: String): Matcher<FullComment?> {
  return object : Matcher<FullComment?> {
    override fun test(value: FullComment?): MatcherResult {
      value!!
      return MatcherResult.invoke(value.text == text, { "text should be '$text' but was '${value.text}'" }, { "text should not be '$text' but was '${value.text}'" })
    }
  }
}

fun scoreMatch(score: Long): Matcher<FullComment?> {
  return object : Matcher<FullComment?> {
    override fun test(value: FullComment?): MatcherResult {
      value!!
      return MatcherResult.invoke(value.score == score, { "score should be $score but was ${value.score}" }, { "score should not be $score but was ${value.score}" })
    }
  }
}

fun voteMatch(vote: Int): Matcher<FullComment?> {
  return object : Matcher<FullComment?> {
    override fun test(value: FullComment?): MatcherResult {
      value!!
      return MatcherResult.invoke(value.vote == vote, { "vote should be $vote but was ${value.vote}" }, { "vote should not be $vote but was ${value.vote}" })
    }
  }
}

fun replyCountMatch(replyCount: Int): Matcher<FullComment?> {
  return object : Matcher<FullComment?> {
    override fun test(value: FullComment?): MatcherResult {
      return MatcherResult.invoke(value?.replyCount == replyCount, { "reply count should be $replyCount but was ${value?.replyCount}" }, { "reply count should not be $replyCount but was ${value?.replyCount}" })
    }
  }
}
