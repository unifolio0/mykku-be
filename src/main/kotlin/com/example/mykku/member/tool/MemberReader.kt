package com.example.mykku.member.tool

import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.FollowRepository
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class MemberReader(
    private val followRepository: FollowRepository,
    private val memberRepository: MemberRepository
) {
    fun getFollowerByMemberId(memberId: String): List<Member> {
        return followRepository.findByFollowerId(memberId)
            .map { follow -> follow.following }
    }

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
    
    fun getRecommendedMembersByCommonFollowers(
        memberId: String, 
        minCommonFollowers: Long = 10
    ): List<Member> {
        return followRepository.findRecommendedMembersByCommonFollowers(memberId, minCommonFollowers)
    }
}
