package com.example.mykku.member.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberWriter(
    private val memberRepository: MemberRepository
) {
    fun save(member: Member): Member {
        return memberRepository.save(member)
    }
}
