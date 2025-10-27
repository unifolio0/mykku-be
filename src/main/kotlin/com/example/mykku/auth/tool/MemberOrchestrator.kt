package com.example.mykku.auth.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import org.springframework.stereotype.Component

@Component
class MemberOrchestrator(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter
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
        return memberWriter.save(
            Member(
                id = memberInfo.memberId,
                nickname = memberInfo.nickname,
                role = "USER",
                profileImage = memberInfo.profileImage,
                provider = memberInfo.provider,
                socialId = memberInfo.socialId,
                email = memberInfo.email
            )
        )
    }
}
