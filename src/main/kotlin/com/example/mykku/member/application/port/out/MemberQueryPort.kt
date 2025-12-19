package com.example.mykku.member.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.Email
import com.example.mykku.member.domain.model.MemberId

data class MemberSummary(
    val id: String,
    val nickname: String,
    val profileImage: String
)

interface MemberQueryPort {
    fun findSummaryById(id: MemberId): MemberSummary?
    fun existsById(id: MemberId): Boolean
    fun existsByEmail(email: Email): Boolean
    fun existsByEmailString(email: String): Boolean
    fun findSummaryByEmail(email: Email): MemberSummary?
    fun getFollowingMemberIds(memberId: MemberId): List<MemberId>
    fun getRecommendedMemberIdsByCommonFollowers(memberId: MemberId, minCommonFollowers: Long = 10): List<MemberId>

    fun getMemberById(memberId: MemberId): Member
    fun findMemberById(memberId: String): Member?
    fun findMemberByEmail(email: String): Member?
    fun getFollowingMembers(memberId: MemberId): List<Member>
    fun getRecommendedMembersByCommonFollowers(memberId: MemberId, minCommonFollowers: Long = 10): List<Member>
    fun existsByRoleId(roleId: Long): Boolean
    fun saveMember(member: Member): Member
}
