package com.example.mykku.block

import com.example.mykku.BaseServiceTest
import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.dto.BlockKeywordRequest
import com.example.mykku.block.dto.BlockMemberRequest
import com.example.mykku.block.exception.BlockErrorCode
import com.example.mykku.block.exception.BlockException
import com.example.mykku.block.tool.BlockReader
import com.example.mykku.block.tool.BlockWriter
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals

class BlockServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var blockReader: BlockReader

    @Mock
    private lateinit var blockWriter: BlockWriter

    @Mock
    private lateinit var memberReader: MemberReader

    @InjectMocks
    private lateinit var blockService: BlockService

    @Test
    fun `blockMember는 사용자를 차단하고 응답을 반환한다`() {
        // given
        val blocker = createTestMember(id = "blocker")
        val blocked = createTestMember(id = "blocked")
        val request = BlockMemberRequest(memberId = "blocked")
        val memberBlock = MemberBlock(id = 1L, blocker = blocker, blocked = blocked)
            .also { initializeBaseEntityFields(it) }

        whenever(memberReader.getMemberById("blocked")).thenReturn(blocked)
        doNothing().whenever(blockReader).validateMemberBlockNotExists(blocker, blocked)
        whenever(blockWriter.createMemberBlock(blocker, blocked)).thenReturn(memberBlock)

        // when
        val result = blockService.blockMember(blocker, request)

        // then
        assertEquals("blocked", result.blockedMemberId)
        verify(blockWriter).createMemberBlock(blocker, blocked)
    }

    @Test
    fun `blockMember는 자기 자신을 차단하려 하면 예외를 발생시킨다`() {
        // given
        val member = createTestMember(id = "member")
        val request = BlockMemberRequest(memberId = "member")

        // when & then
        val exception = assertThrows<BlockException> {
            blockService.blockMember(member, request)
        }
        assertEquals(BlockErrorCode.CANNOT_BLOCK_SELF, exception.errorCode)
    }

    @Test
    fun `blockMember는 존재하지 않는 사용자를 차단하려 하면 예외를 발생시킨다`() {
        // given
        val blocker = createTestMember(id = "blocker")
        val request = BlockMemberRequest(memberId = "nonexistent")

        whenever(memberReader.getMemberById("nonexistent"))
            .thenThrow(MemberException.memberNotFound())

        // when & then
        val exception = assertThrows<BlockException> {
            blockService.blockMember(blocker, request)
        }
        assertEquals(BlockErrorCode.MEMBER_TO_BLOCK_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `unblockMember는 사용자 차단을 해제한다`() {
        // given
        val blocker = createTestMember(id = "blocker")
        val blocked = createTestMember(id = "blocked")

        whenever(memberReader.getMemberById("blocked")).thenReturn(blocked)
        doNothing().whenever(blockReader).validateMemberBlockExists(blocker, blocked)
        doNothing().whenever(blockWriter).deleteMemberBlock(blocker, blocked)

        // when
        blockService.unblockMember(blocker, "blocked")

        // then
        verify(blockWriter).deleteMemberBlock(blocker, blocked)
    }

    @Test
    fun `getMemberBlocks는 차단한 사용자 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val pageable = PageRequest.of(0, 10)
        val blocked = createTestMember(id = "blocked", nickname = "차단유저")
        val memberBlock = MemberBlock(id = 1L, blocker = member, blocked = blocked)
            .also { initializeBaseEntityFields(it) }
        val page = PageImpl(listOf(memberBlock), pageable, 1)

        whenever(blockReader.getMemberBlocks(member, pageable)).thenReturn(page)

        // when
        val result = blockService.getMemberBlocks(member, pageable)

        // then
        assertEquals(1, result.totalCount)
        assertEquals(1, result.blocks.size)
    }

    @Test
    fun `blockKeyword는 키워드를 차단하고 응답을 반환한다`() {
        // given
        val member = createTestMember()
        val request = BlockKeywordRequest(keyword = "스포일러")
        val keywordBlock = KeywordBlock(id = 1L, member = member, keyword = "스포일러")
            .also { initializeBaseEntityFields(it) }

        doNothing().whenever(blockReader).validateKeywordBlockNotExists(member, "스포일러")
        doNothing().whenever(blockReader).validateKeywordLimit(member)
        whenever(blockWriter.createKeywordBlock(member, "스포일러")).thenReturn(keywordBlock)

        // when
        val result = blockService.blockKeyword(member, request)

        // then
        assertEquals("스포일러", result.keyword)
        verify(blockWriter).createKeywordBlock(member, "스포일러")
    }

    @Test
    fun `blockKeyword는 키워드를 정규화한다 (trim + lowercase)`() {
        // given
        val member = createTestMember()
        val request = BlockKeywordRequest(keyword = "  SPOILER  ")
        val keywordBlock = KeywordBlock(id = 1L, member = member, keyword = "spoiler")
            .also { initializeBaseEntityFields(it) }

        doNothing().whenever(blockReader).validateKeywordBlockNotExists(member, "spoiler")
        doNothing().whenever(blockReader).validateKeywordLimit(member)
        whenever(blockWriter.createKeywordBlock(member, "spoiler")).thenReturn(keywordBlock)

        // when
        val result = blockService.blockKeyword(member, request)

        // then
        assertEquals("spoiler", result.keyword)
    }

    @Test
    fun `blockKeyword는 빈 키워드면 예외를 발생시킨다`() {
        // given
        val member = createTestMember()
        val request = BlockKeywordRequest(keyword = "   ")

        // when & then
        val exception = assertThrows<BlockException> {
            blockService.blockKeyword(member, request)
        }
        assertEquals(BlockErrorCode.KEYWORD_EMPTY, exception.errorCode)
    }

    @Test
    fun `blockKeyword는 키워드가 50자를 초과하면 예외를 발생시킨다`() {
        // given
        val member = createTestMember()
        val request = BlockKeywordRequest(keyword = "a".repeat(51))

        // when & then
        val exception = assertThrows<BlockException> {
            blockService.blockKeyword(member, request)
        }
        assertEquals(BlockErrorCode.KEYWORD_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `unblockKeyword는 키워드 차단을 해제한다`() {
        // given
        val member = createTestMember()

        doNothing().whenever(blockReader).validateKeywordBlockExists(member, "스포일러")
        doNothing().whenever(blockWriter).deleteKeywordBlock(member, "스포일러")

        // when
        blockService.unblockKeyword(member, "스포일러")

        // then
        verify(blockWriter).deleteKeywordBlock(member, "스포일러")
    }

    @Test
    fun `getKeywordBlocks는 차단한 키워드 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val pageable = PageRequest.of(0, 10)
        val keywordBlock = KeywordBlock(id = 1L, member = member, keyword = "스포일러")
            .also { initializeBaseEntityFields(it) }
        val page = PageImpl(listOf(keywordBlock), pageable, 1)

        whenever(blockReader.getKeywordBlocks(member, pageable)).thenReturn(page)

        // when
        val result = blockService.getKeywordBlocks(member, pageable)

        // then
        assertEquals(1, result.totalCount)
        assertEquals(1, result.blocks.size)
    }
}
