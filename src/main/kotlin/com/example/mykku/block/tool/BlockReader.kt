package com.example.mykku.block.tool

import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.exception.BlockException
import com.example.mykku.block.repository.KeywordBlockRepository
import com.example.mykku.block.repository.MemberBlockRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BlockReader(
    private val memberBlockRepository: MemberBlockRepository,
    private val keywordBlockRepository: KeywordBlockRepository
) {

    fun getBlockedMemberIds(blockerId: String): Set<String> {
        return memberBlockRepository.findBlockedMemberIdsByBlockerId(blockerId).toSet()
    }

    fun getBlockerMemberIds(blockedId: String): Set<String> {
        return memberBlockRepository.findBlockerMemberIdsByBlockedId(blockedId).toSet()
    }

    fun getAllBlockRelatedMemberIds(memberId: String): Set<String> {
        val blockedByMe = getBlockedMemberIds(memberId)
        val blockedMe = getBlockerMemberIds(memberId)
        return blockedByMe + blockedMe
    }

    fun getBlockedKeywords(memberId: String): List<String> {
        return keywordBlockRepository.findKeywordsByMemberId(memberId)
    }

    fun getMemberBlocks(blocker: Member, pageable: Pageable): Page<MemberBlock> {
        return memberBlockRepository.findAllByBlocker(blocker, pageable)
    }

    fun getKeywordBlocks(member: Member, pageable: Pageable): Page<KeywordBlock> {
        return keywordBlockRepository.findAllByMember(member, pageable)
    }

    fun isMemberBlocked(blocker: Member, blocked: Member): Boolean {
        return memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)
    }

    fun isKeywordBlocked(member: Member, keyword: String): Boolean {
        return keywordBlockRepository.existsByMemberAndKeyword(member, keyword)
    }

    fun getMemberBlockCount(blocker: Member): Long {
        return memberBlockRepository.countByBlocker(blocker)
    }

    fun getKeywordBlockCount(member: Member): Long {
        return keywordBlockRepository.countByMember(member)
    }

    fun validateMemberBlockExists(blocker: Member, blocked: Member) {
        if (!memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw BlockException.memberBlockNotFound()
        }
    }

    fun validateMemberBlockNotExists(blocker: Member, blocked: Member) {
        if (memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw BlockException.memberAlreadyBlocked()
        }
    }

    fun validateKeywordBlockExists(member: Member, keyword: String) {
        if (!keywordBlockRepository.existsByMemberAndKeyword(member, keyword)) {
            throw BlockException.keywordBlockNotFound()
        }
    }

    fun validateKeywordBlockNotExists(member: Member, keyword: String) {
        if (keywordBlockRepository.existsByMemberAndKeyword(member, keyword)) {
            throw BlockException.keywordAlreadyBlocked()
        }
    }

    fun validateKeywordLimit(member: Member) {
        if (keywordBlockRepository.countByMember(member) >= KeywordBlock.MAX_KEYWORD_COUNT) {
            throw BlockException.keywordLimitExceeded()
        }
    }
}
