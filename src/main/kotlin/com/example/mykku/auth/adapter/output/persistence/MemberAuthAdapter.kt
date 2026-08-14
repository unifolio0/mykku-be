package com.example.mykku.auth.adapter.output.persistence

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.auth.application.dto.OAuthMemberInfo
import com.example.mykku.auth.application.port.output.MemberAuthPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.stereotype.Component

@Component
class MemberAuthAdapter(
    private val memberRepository: MemberRepository,
    private val activityEventPublisher: ActivityEventPublisher
) : MemberAuthPort {

    override fun findOrCreate(memberInfo: OAuthMemberInfo): Pair<Member, Boolean> {
        val existingMember = memberRepository.findByProviderAndSocialId(memberInfo.provider, memberInfo.socialId)

        if (existingMember != null) {
            return Pair(existingMember, true)
        }

        val newMember = createMember(memberInfo)
        activityEventPublisher.publish(ActivityEvent(newMember.id.value, ActivityType.FIRST_LOGIN))
        return Pair(newMember, false)
    }

    override fun findById(memberId: Long): Member? {
        return memberRepository.findById(MemberPk(memberId))
    }

    private fun createMember(memberInfo: OAuthMemberInfo): Member {
        val member = Member.createSocialMember(
            profileImage = memberInfo.profileImage,
            provider = memberInfo.provider,
            socialId = memberInfo.socialId,
            email = memberInfo.email
        )

        return memberRepository.save(member)
    }
}
