package com.example.mykku.block.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.exception.BlockErrorCode
import com.example.mykku.block.exception.BlockException
import com.example.mykku.block.repository.KeywordBlockRepository
import com.example.mykku.block.repository.MemberBlockRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BlockReaderTest : BaseToolTest() {

    @Mock
    private lateinit var memberBlockRepository: MemberBlockRepository

    @Mock
    private lateinit var keywordBlockRepository: KeywordBlockRepository

    @InjectMocks
    private lateinit var blockReader: BlockReader

    @Test
    fun `getBlockedMemberIds는 차단한 사용자 ID 목록을 반환한다`() {
        // given
        val blockerId = "blocker"
        whenever(memberBlockRepository.findBlockedMemberIdsByBlockerId(blockerId))
            .thenReturn(listOf("blocked1", "blocked2"))

        // when
        val result = blockReader.getBlockedMemberIds(blockerId)

        // then
        assertEquals(setOf("blocked1", "blocked2"), result)
    }

    @Test
    fun `getBlockerMemberIds는 나를 차단한 사용자 ID 목록을 반환한다`() {
        // given
        val blockedId = "blocked"
        whenever(memberBlockRepository.findBlockerMemberIdsByBlockedId(blockedId))
            .thenReturn(listOf("blocker1", "blocker2"))

        // when
        val result = blockReader.getBlockerMemberIds(blockedId)

        // then
        assertEquals(setOf("blocker1", "blocker2"), result)
    }

    @Test
    fun `getAllBlockRelatedMemberIds는 양방향 차단 관련 ID를 모두 반환한다`() {
        // given
        val memberId = "member"
        whenever(memberBlockRepository.findBlockedMemberIdsByBlockerId(memberId))
            .thenReturn(listOf("blocked1", "blocked2"))
        whenever(memberBlockRepository.findBlockerMemberIdsByBlockedId(memberId))
            .thenReturn(listOf("blocker1", "blocker2"))

        // when
        val result = blockReader.getAllBlockRelatedMemberIds(memberId)

        // then
        assertEquals(setOf("blocked1", "blocked2", "blocker1", "blocker2"), result)
    }

    @Test
    fun `getBlockedKeywords는 차단한 키워드 목록을 반환한다`() {
        // given
        val memberId = "member"
        whenever(keywordBlockRepository.findKeywordsByMemberId(memberId))
            .thenReturn(listOf("스포일러", "광고"))

        // when
        val result = blockReader.getBlockedKeywords(memberId)

        // then
        assertEquals(listOf("스포일러", "광고"), result)
    }

    @Test
    fun `getMemberBlocks는 차단한 사용자 목록을 페이징으로 반환한다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")
        val memberBlock = MemberBlock(id = 1L, blocker = blocker, blocked = blocked)
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(memberBlock), pageable, 1)

        whenever(memberBlockRepository.findAllByBlocker(blocker, pageable))
            .thenReturn(page)

        // when
        val result = blockReader.getMemberBlocks(blocker, pageable)

        // then
        assertEquals(1, result.totalElements)
        assertEquals(1, result.content.size)
    }

    @Test
    fun `getKeywordBlocks는 차단한 키워드 목록을 페이징으로 반환한다`() {
        // given
        val member = createMockMember()
        val keywordBlock = KeywordBlock(id = 1L, member = member, keyword = "스포일러")
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(keywordBlock), pageable, 1)

        whenever(keywordBlockRepository.findAllByMember(member, pageable))
            .thenReturn(page)

        // when
        val result = blockReader.getKeywordBlocks(member, pageable)

        // then
        assertEquals(1, result.totalElements)
        assertEquals(1, result.content.size)
    }

    @Test
    fun `isMemberBlocked는 차단 여부를 반환한다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")

        whenever(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked))
            .thenReturn(true)

        // when
        val result = blockReader.isMemberBlocked(blocker, blocked)

        // then
        assertTrue(result)
    }

    @Test
    fun `isKeywordBlocked는 키워드 차단 여부를 반환한다`() {
        // given
        val member = createMockMember()

        whenever(keywordBlockRepository.existsByMemberAndKeyword(member, "스포일러"))
            .thenReturn(true)

        // when
        val result = blockReader.isKeywordBlocked(member, "스포일러")

        // then
        assertTrue(result)
    }

    @Test
    fun `validateMemberBlockExists는 차단이 존재하지 않으면 예외를 발생시킨다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")

        whenever(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked))
            .thenReturn(false)

        // when & then
        val exception = assertThrows<BlockException> {
            blockReader.validateMemberBlockExists(blocker, blocked)
        }
        assertEquals(BlockErrorCode.MEMBER_BLOCK_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateMemberBlockNotExists는 이미 차단된 경우 예외를 발생시킨다`() {
        // given
        val blocker = createMockMember(id = "blocker")
        val blocked = createMockMember(id = "blocked")

        whenever(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked))
            .thenReturn(true)

        // when & then
        val exception = assertThrows<BlockException> {
            blockReader.validateMemberBlockNotExists(blocker, blocked)
        }
        assertEquals(BlockErrorCode.MEMBER_ALREADY_BLOCKED, exception.errorCode)
    }

    @Test
    fun `validateKeywordBlockExists는 키워드 차단이 존재하지 않으면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()

        whenever(keywordBlockRepository.existsByMemberAndKeyword(member, "스포일러"))
            .thenReturn(false)

        // when & then
        val exception = assertThrows<BlockException> {
            blockReader.validateKeywordBlockExists(member, "스포일러")
        }
        assertEquals(BlockErrorCode.KEYWORD_BLOCK_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateKeywordBlockNotExists는 이미 차단된 키워드면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()

        whenever(keywordBlockRepository.existsByMemberAndKeyword(member, "스포일러"))
            .thenReturn(true)

        // when & then
        val exception = assertThrows<BlockException> {
            blockReader.validateKeywordBlockNotExists(member, "스포일러")
        }
        assertEquals(BlockErrorCode.KEYWORD_ALREADY_BLOCKED, exception.errorCode)
    }

    @Test
    fun `validateKeywordLimit는 키워드 제한 초과시 예외를 발생시킨다`() {
        // given
        val member = createMockMember()

        whenever(keywordBlockRepository.countByMember(member))
            .thenReturn(KeywordBlock.MAX_KEYWORD_COUNT.toLong())

        // when & then
        val exception = assertThrows<BlockException> {
            blockReader.validateKeywordLimit(member)
        }
        assertEquals(BlockErrorCode.KEYWORD_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `validateKeywordLimit는 제한 미만이면 예외를 발생시키지 않는다`() {
        // given
        val member = createMockMember()

        whenever(keywordBlockRepository.countByMember(member))
            .thenReturn(50L)

        // when & then (no exception)
        blockReader.validateKeywordLimit(member)
    }
}
