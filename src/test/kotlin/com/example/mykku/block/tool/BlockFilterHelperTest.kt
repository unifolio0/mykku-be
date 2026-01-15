package com.example.mykku.block.tool

import com.example.mykku.BaseToolTest
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class BlockFilterHelperTest : BaseToolTest() {

    @Mock
    private lateinit var blockReader: BlockReader

    @InjectMocks
    private lateinit var blockFilterHelper: BlockFilterHelper

    data class TestContent(
        val id: Long,
        val authorId: String,
        val title: String,
        val content: String
    )

    @Test
    fun `filterContent는 memberId가 null이면 원본 리스트를 반환한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "제목1", "내용1"),
            TestContent(2L, "author2", "제목2", "내용2")
        )

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = null,
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(2, result.size)
    }

    @Test
    fun `filterContent는 빈 리스트이면 빈 리스트를 반환한다`() {
        // given
        val items = emptyList<TestContent>()

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = "member",
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun `filterContent는 차단된 사용자의 콘텐츠를 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "제목1", "내용1"),
            TestContent(2L, "blockedAuthor", "제목2", "내용2"),
            TestContent(3L, "author3", "제목3", "내용3")
        )
        val memberId = "member"

        whenever(blockReader.getAllBlockRelatedMemberIds(memberId))
            .thenReturn(setOf("blockedAuthor"))
        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(emptyList())

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = memberId,
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(2, result.size)
        assertEquals(listOf(1L, 3L), result.map { it.id })
    }

    @Test
    fun `filterContent는 차단된 키워드가 포함된 콘텐츠를 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "일반 제목", "일반 내용"),
            TestContent(2L, "author2", "스포일러 포함", "일반 내용"),
            TestContent(3L, "author3", "일반 제목", "광고가 포함된 내용")
        )
        val memberId = "member"

        whenever(blockReader.getAllBlockRelatedMemberIds(memberId))
            .thenReturn(emptySet())
        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(listOf("스포일러", "광고"))

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = memberId,
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
    }

    @Test
    fun `filterContent는 키워드를 대소문자 구분 없이 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "SPOILER 제목", "내용"),
            TestContent(2L, "author2", "일반 제목", "내용")
        )
        val memberId = "member"

        whenever(blockReader.getAllBlockRelatedMemberIds(memberId))
            .thenReturn(emptySet())
        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(listOf("spoiler"))

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = memberId,
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(1, result.size)
        assertEquals(2L, result[0].id)
    }

    @Test
    fun `filterContent는 양방향 차단을 모두 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "blockedByMe", "제목1", "내용1"),
            TestContent(2L, "blockedMe", "제목2", "내용2"),
            TestContent(3L, "normalUser", "제목3", "내용3")
        )
        val memberId = "member"

        whenever(blockReader.getAllBlockRelatedMemberIds(memberId))
            .thenReturn(setOf("blockedByMe", "blockedMe"))
        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(emptyList())

        // when
        val result = blockFilterHelper.filterContent(
            items = items,
            memberId = memberId,
            memberIdExtractor = { it.authorId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(1, result.size)
        assertEquals("normalUser", result[0].authorId)
    }

    @Test
    fun `filterByBlockedMembers는 차단된 사용자만 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "스포일러 제목", "내용"),
            TestContent(2L, "blockedAuthor", "일반 제목", "내용")
        )
        val memberId = "member"

        whenever(blockReader.getAllBlockRelatedMemberIds(memberId))
            .thenReturn(setOf("blockedAuthor"))

        // when
        val result = blockFilterHelper.filterByBlockedMembers(
            items = items,
            memberId = memberId,
            memberIdExtractor = { it.authorId }
        )

        // then
        assertEquals(1, result.size)
        assertEquals("author1", result[0].authorId)
    }

    @Test
    fun `filterByBlockedKeywords는 차단된 키워드만 필터링한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "스포일러 제목", "내용"),
            TestContent(2L, "author2", "일반 제목", "내용")
        )
        val memberId = "member"

        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(listOf("스포일러"))

        // when
        val result = blockFilterHelper.filterByBlockedKeywords(
            items = items,
            memberId = memberId,
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(1, result.size)
        assertEquals("author2", result[0].authorId)
    }

    @Test
    fun `filterByBlockedKeywords는 차단 키워드가 없으면 원본을 반환한다`() {
        // given
        val items = listOf(
            TestContent(1L, "author1", "제목1", "내용1"),
            TestContent(2L, "author2", "제목2", "내용2")
        )
        val memberId = "member"

        whenever(blockReader.getBlockedKeywords(memberId))
            .thenReturn(emptyList())

        // when
        val result = blockFilterHelper.filterByBlockedKeywords(
            items = items,
            memberId = memberId,
            contentExtractors = listOf({ it.title }, { it.content })
        )

        // then
        assertEquals(2, result.size)
    }
}
