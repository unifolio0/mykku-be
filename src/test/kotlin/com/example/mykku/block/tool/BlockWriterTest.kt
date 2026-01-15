package com.example.mykku.block.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.repository.KeywordBlockRepository
import com.example.mykku.block.repository.MemberBlockRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class BlockWriterTest : BaseToolTest() {

    @Mock
    private lateinit var memberBlockRepository: MemberBlockRepository

    @Mock
    private lateinit var keywordBlockRepository: KeywordBlockRepository

    @InjectMocks
    private lateinit var blockWriter: BlockWriter

    @Test
    fun `createMemberBlock은 사용자 차단을 생성한다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")
        val memberBlock = MemberBlock(id = 1L, blocker = blocker, blocked = blocked)

        whenever(memberBlockRepository.save(any<MemberBlock>()))
            .thenReturn(memberBlock)

        // when
        val result = blockWriter.createMemberBlock(blocker, blocked)

        // then
        assertEquals(1L, result.id)
        assertEquals(blocker, result.blocker)
        assertEquals(blocked, result.blocked)
        verify(memberBlockRepository).save(any<MemberBlock>())
    }

    @Test
    fun `deleteMemberBlock은 사용자 차단을 삭제한다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")

        // when
        blockWriter.deleteMemberBlock(blocker, blocked)

        // then
        verify(memberBlockRepository).deleteByBlockerAndBlocked(blocker, blocked)
    }

    @Test
    fun `createKeywordBlock은 키워드 차단을 생성한다`() {
        // given
        val member = createMockMember()
        val keyword = "스포일러"
        val keywordBlock = KeywordBlock(id = 1L, member = member, keyword = keyword)

        whenever(keywordBlockRepository.save(any<KeywordBlock>()))
            .thenReturn(keywordBlock)

        // when
        val result = blockWriter.createKeywordBlock(member, keyword)

        // then
        assertEquals(1L, result.id)
        assertEquals(member, result.member)
        assertEquals(keyword, result.keyword)
        verify(keywordBlockRepository).save(any<KeywordBlock>())
    }

    @Test
    fun `deleteKeywordBlock은 키워드 차단을 삭제한다`() {
        // given
        val member = createMockMember()
        val keyword = "스포일러"

        // when
        blockWriter.deleteKeywordBlock(member, keyword)

        // then
        verify(keywordBlockRepository).deleteByMemberAndKeyword(member, keyword)
    }
}
