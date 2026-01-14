package com.example.mykku.block.tool

import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.repository.KeywordBlockRepository
import com.example.mykku.block.repository.MemberBlockRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class BlockWriter(
    private val memberBlockRepository: MemberBlockRepository,
    private val keywordBlockRepository: KeywordBlockRepository
) {

    fun createMemberBlock(blocker: Member, blocked: Member): MemberBlock {
        val memberBlock = MemberBlock(
            blocker = blocker,
            blocked = blocked
        )
        return memberBlockRepository.save(memberBlock)
    }

    fun deleteMemberBlock(blocker: Member, blocked: Member) {
        memberBlockRepository.deleteByBlockerAndBlocked(blocker, blocked)
    }

    fun createKeywordBlock(member: Member, keyword: String): KeywordBlock {
        val keywordBlock = KeywordBlock(
            member = member,
            keyword = keyword
        )
        return keywordBlockRepository.save(keywordBlock)
    }

    fun deleteKeywordBlock(member: Member, keyword: String) {
        keywordBlockRepository.deleteByMemberAndKeyword(member, keyword)
    }
}
