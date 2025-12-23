package com.example.mykku.member.infrastructure.adapter

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.application.port.out.MemberSummary
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.Email
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.repository.FollowRepository
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberQueryAdapter(
    private val memberRepository: MemberRepository,
    private val followRepository: FollowRepository
) : MemberQueryPort {

    override fun findSummaryById(id: MemberId): MemberSummary? {
        return memberRepository.findById(id.value)
            .map { member ->
                MemberSummary(
                    id = member.id,
                    nickname = member.nickname,
                    profileImage = member.profileImage
                )
            }
            .orElse(null)
    }

    override fun existsById(id: MemberId): Boolean {
        return memberRepository.existsById(id.value)
    }

    override fun existsByEmail(email: Email): Boolean {
        return memberRepository.existsByEmail(email.value)
    }

    override fun existsByEmailString(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }

    override fun findSummaryByEmail(email: Email): MemberSummary? {
        return memberRepository.findByEmail(email.value)?.let { member ->
            MemberSummary(
                id = member.id,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        }
    }

    override fun getFollowingMemberIds(memberId: MemberId): List<MemberId> {
        return followRepository.findByFollowerId(memberId.value)
            .map { follow -> MemberId(follow.following.id) }
    }

    override fun getRecommendedMemberIdsByCommonFollowers(memberId: MemberId, minCommonFollowers: Long): List<MemberId> {
        return followRepository.findRecommendedMembersByCommonFollowers(memberId.value, minCommonFollowers)
            .map { member -> MemberId(member.id) }
    }

    override fun getMemberById(memberId: MemberId): Member {
        return memberRepository.findById(memberId.value)
            .orElseThrow { MemberException.memberNotFound() }
    }

    override fun findMemberByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    override fun getFollowingMembers(memberId: MemberId): List<Member> {
        return followRepository.findByFollowerId(memberId.value)
            .map { follow -> follow.following }
    }

    override fun getRecommendedMembersByCommonFollowers(memberId: MemberId, minCommonFollowers: Long): List<Member> {
        return followRepository.findRecommendedMembersByCommonFollowers(memberId.value, minCommonFollowers)
    }

    override fun existsByRoleId(roleId: Long): Boolean {
        return memberRepository.existsByRoleId(roleId)
    }

    override fun findMemberById(memberId: String): Member? {
        return memberRepository.findById(memberId).orElse(null)
    }

    override fun saveMember(member: Member): Member {
        return memberRepository.save(member)
    }
}
