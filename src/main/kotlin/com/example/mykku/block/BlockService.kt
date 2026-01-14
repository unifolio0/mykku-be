package com.example.mykku.block

import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.dto.BlockKeywordRequest
import com.example.mykku.block.dto.BlockMemberRequest
import com.example.mykku.block.dto.KeywordBlockListResponse
import com.example.mykku.block.dto.KeywordBlockResponse
import com.example.mykku.block.dto.MemberBlockListResponse
import com.example.mykku.block.dto.MemberBlockResponse
import com.example.mykku.block.exception.BlockException
import com.example.mykku.block.tool.BlockReader
import com.example.mykku.block.tool.BlockWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberReader
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockService(
    private val blockReader: BlockReader,
    private val blockWriter: BlockWriter,
    private val memberReader: MemberReader
) {

    @Transactional
    fun blockMember(blocker: Member, request: BlockMemberRequest): MemberBlockResponse {
        validateNotSelf(blocker, request.memberId)

        val blocked = try {
            memberReader.getMemberById(request.memberId)
        } catch (e: MemberException) {
            throw BlockException.memberToBlockNotFound()
        }

        blockReader.validateMemberBlockNotExists(blocker, blocked)

        val memberBlock = blockWriter.createMemberBlock(blocker, blocked)
        return MemberBlockResponse.from(memberBlock)
    }

    @Transactional
    fun unblockMember(blocker: Member, blockedMemberId: String) {
        val blocked = try {
            memberReader.getMemberById(blockedMemberId)
        } catch (e: MemberException) {
            throw BlockException.memberBlockNotFound()
        }

        blockReader.validateMemberBlockExists(blocker, blocked)
        blockWriter.deleteMemberBlock(blocker, blocked)
    }

    @Transactional(readOnly = true)
    fun getMemberBlocks(member: Member, pageable: Pageable): MemberBlockListResponse {
        val page = blockReader.getMemberBlocks(member, pageable)
        return MemberBlockListResponse.from(page)
    }

    @Transactional
    fun blockKeyword(member: Member, request: BlockKeywordRequest): KeywordBlockResponse {
        val normalizedKeyword = normalizeKeyword(request.keyword)
        validateKeyword(normalizedKeyword)

        blockReader.validateKeywordBlockNotExists(member, normalizedKeyword)
        blockReader.validateKeywordLimit(member)

        val keywordBlock = blockWriter.createKeywordBlock(member, normalizedKeyword)
        return KeywordBlockResponse.from(keywordBlock)
    }

    @Transactional
    fun unblockKeyword(member: Member, keyword: String) {
        val normalizedKeyword = normalizeKeyword(keyword)
        blockReader.validateKeywordBlockExists(member, normalizedKeyword)
        blockWriter.deleteKeywordBlock(member, normalizedKeyword)
    }

    @Transactional(readOnly = true)
    fun getKeywordBlocks(member: Member, pageable: Pageable): KeywordBlockListResponse {
        val page = blockReader.getKeywordBlocks(member, pageable)
        return KeywordBlockListResponse.from(page)
    }

    private fun validateNotSelf(blocker: Member, blockedMemberId: String) {
        if (blocker.id == blockedMemberId) {
            throw BlockException.cannotBlockSelf()
        }
    }

    private fun normalizeKeyword(keyword: String): String {
        return keyword.trim().lowercase()
    }

    private fun validateKeyword(keyword: String) {
        if (keyword.isBlank()) {
            throw BlockException.keywordEmpty()
        }
        if (keyword.length > KeywordBlock.KEYWORD_MAX_LENGTH) {
            throw BlockException.keywordTooLong()
        }
    }
}
