package com.example.mykku.auth.infrastructure.adapter

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.role.application.port.out.MemberRoleRepositoryPort
import com.example.mykku.role.application.port.out.RoleQueryPort
import org.springframework.stereotype.Component

@Component
class MemberOrchestrator(
    private val memberQueryPort: MemberQueryPort,
    private val roleQueryPort: RoleQueryPort,
    private val memberRoleRepositoryPort: MemberRoleRepositoryPort
) {

    fun findOrCreate(memberInfo: OAuthMemberInfo): Pair<Member, Boolean> {
        val existingMember = memberQueryPort.findMemberById(memberInfo.memberId)

        return if (existingMember != null) {
            Pair(existingMember, true)
        } else {
            val newMember = createMember(memberInfo)
            Pair(newMember, false)
        }
    }

    private fun createMember(memberInfo: OAuthMemberInfo): Member {
        val member = Member.createSocialMember(
            id = memberInfo.memberId,
            nickname = memberInfo.nickname,
            profileImage = memberInfo.profileImage,
            provider = memberInfo.provider,
            socialId = memberInfo.socialId,
            email = memberInfo.email
        )

        memberQueryPort.saveMember(member)
        return member
    }
}
