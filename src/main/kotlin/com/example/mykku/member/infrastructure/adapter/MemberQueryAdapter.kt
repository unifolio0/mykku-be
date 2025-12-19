package com.example.mykku.member.infrastructure.adapter

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.application.port.out.MemberSummary
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberQueryAdapter(
    private val memberRepository: MemberRepository
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
}
