package com.example.mykku.auth.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import com.example.mykku.role.tool.MemberRoleWriter
import com.example.mykku.role.tool.RoleReader
import org.springframework.stereotype.Component

@Component
class MemberOrchestrator(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val roleReader: RoleReader,
    private val memberRoleWriter: MemberRoleWriter
) {

    fun findOrCreate(memberInfo: OAuthMemberInfo): Pair<Member, Boolean> {
        val existingMember = memberReader.findById(memberInfo.memberId)

        return if (existingMember.isPresent) {
            Pair(existingMember.get(), true)
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

        memberWriter.save(member)
        return member
    }
}
