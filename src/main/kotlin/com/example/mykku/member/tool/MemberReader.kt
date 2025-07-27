package com.example.mykku.member.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
            .orElseThrow { MykkuException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    fun findById(memberId: String): Optional<Member> {
        return memberRepository.findById(memberId)
    }

    fun existsByNickname(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
    }
}
