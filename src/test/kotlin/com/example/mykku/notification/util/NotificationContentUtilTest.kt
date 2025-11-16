package com.example.mykku.notification.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationContentUtilTest {

    @Test
    fun `buildContent는 짧은 내용을 그대로 반환한다`() {
        val template = "%s님이 댓글을 남겼습니다: "
        val userContent = "좋은 글이네요!"
        val nickname = "홍길동"

        val result = NotificationContentUtil.buildContent(template, userContent, nickname)

        assertEquals("홍길동님이 댓글을 남겼습니다: 좋은 글이네요!", result)
        assertTrue(result.length <= 500)
    }

    @Test
    fun `buildContent는 제한 길이와 정확히 같은 내용을 그대로 반환한다`() {
        val template = "%s님이 댓글을 남겼습니다: "
        val nickname = "홍길동"
        val formattedTemplate = template.format(nickname)
        val exactLengthContent = "a".repeat(500 - formattedTemplate.length)

        val result = NotificationContentUtil.buildContent(template, exactLengthContent, nickname)

        assertEquals(500, result.length)
        assertTrue(result.endsWith("a"))
    }

    @Test
    fun `buildContent는 긴 내용을 잘라내고 생략 기호를 추가한다`() {
        val template = "%s님이 댓글을 남겼습니다: "
        val userContent = "a".repeat(1000)
        val nickname = "홍길동"

        val result = NotificationContentUtil.buildContent(template, userContent, nickname)

        assertEquals(500, result.length)
        assertTrue(result.endsWith("..."))
        assertTrue(result.startsWith("홍길동님이 댓글을 남겼습니다: "))
    }

    @Test
    fun `buildContent는 매우 긴 내용도 500자로 제한한다`() {
        val template = "%s님이 회원님의 피드에 댓글을 남겼습니다: "
        val userContent = "정말 멋진 피드네요! ".repeat(100)
        val nickname = "사용자닉네임"

        val result = NotificationContentUtil.buildContent(template, userContent, nickname)

        assertEquals(500, result.length)
        assertTrue(result.endsWith("..."))
    }

    @Test
    fun `buildContent는 빈 내용을 처리한다`() {
        val template = "%s님이 댓글을 남겼습니다: "
        val userContent = ""
        val nickname = "홍길동"

        val result = NotificationContentUtil.buildContent(template, userContent, nickname)

        assertEquals("홍길동님이 댓글을 남겼습니다: ", result)
        assertTrue(result.length <= 500)
    }

    @Test
    fun `buildContent는 특수 문자를 포함한 내용을 처리한다`() {
        val template = "%s님이 새 게시글을 작성했습니다: "
        val userContent = "이모지 테스트 😀😃😄 특수문자 !@#$%^&*()"
        val nickname = "테스터"

        val result = NotificationContentUtil.buildContent(template, userContent, nickname)

        assertTrue(result.contains("이모지 테스트 😀😃😄"))
        assertTrue(result.length <= 500)
    }
}
