package com.example.mykku.member.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.domain.Role
import java.util.Optional
import org.springframework.stereotype.Component

@Component
class MemberReader(
    private val memberRepository: MemberRepository
) {
    fun getMemberById(memberId: String): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { MemberException.memberNotFound() }
    }

    fun findById(memberId: String): Optional<Member> {
        return memberRepository.findById(memberId)
    }

    fun existsByNickname(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
    }

    fun existsByEmail(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }

    fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    fun existsByRole(role: Role): Boolean {
        return memberRepository.existsByRole(role)
    }

    fun existsByMemberId(memberId: String): Boolean {
        return memberRepository.existsByMemberId(memberId)
    }

    fun findByMemberId(memberId: String): Member? {
        return memberRepository.findByMemberId(memberId)
    }

    fun getMemberByMemberId(memberId: String): Member {
        return memberRepository.findByMemberId(memberId)
            ?: throw MemberException.memberNotFound()
    }
}
