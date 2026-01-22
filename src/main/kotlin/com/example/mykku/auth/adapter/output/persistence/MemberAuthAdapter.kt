package com.example.mykku.auth.adapter.output.persistence

import com.example.mykku.auth.application.dto.OAuthMemberInfo
import com.example.mykku.auth.application.port.output.MemberAuthPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Component

@Component
class MemberAuthAdapter(
    private val memberRepository: MemberRepository
) : MemberAuthPort {

    override fun findOrCreate(memberInfo: OAuthMemberInfo): Pair<Member, Boolean> {
        val existingMember = memberRepository.findByIdString(memberInfo.memberId)

        return if (existingMember != null) {
            Pair(existingMember, true)
        } else {
            val newMember = createMember(memberInfo)
            Pair(newMember, false)
        }
    }

    override fun findById(memberId: String): Member? {
        return memberRepository.findByIdString(memberId)
    }

    private fun createMember(memberInfo: OAuthMemberInfo): Member {
        val generatedMemberId = generateMemberId(memberInfo.provider.name, memberInfo.socialId)
        val member = Member.createSocialMember(
            id = memberInfo.memberId,
            memberId = generatedMemberId,
            nickname = memberInfo.nickname,
            profileImage = memberInfo.profileImage,
            provider = memberInfo.provider,
            socialId = memberInfo.socialId,
            email = memberInfo.email
        )

        return memberRepository.save(member)
    }

    private fun generateMemberId(provider: String, socialId: String): String {
        val base = "${provider.lowercase()}${socialId}"
        return base.filter { it.isLetterOrDigit() }.take(16)
    }
}
