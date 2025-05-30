package com.example.mykku.member.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.FollowRepository
import org.springframework.stereotype.Component

@Component
class MemberReader(
    private val followRepository: FollowRepository
) {
    fun getFollowerByMemberId(memberId: String): List<Member> {
        return followRepository.findByFollowerId(memberId)
            .map { follow -> follow.following }
    }
}
