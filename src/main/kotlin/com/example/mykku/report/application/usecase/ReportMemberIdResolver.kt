package com.example.mykku.report.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.stereotype.Component

@Component
class ReportMemberIdResolver(
    private val memberRepository: MemberRepository
) {

    fun resolve(memberPks: List<Long>): Map<Long, String?> {
        val distinctPks = memberPks.distinct().map { MemberPk.of(it) }
        if (distinctPks.isEmpty()) return emptyMap()
        return memberRepository.findByIds(distinctPks).associate { it.id.value to it.memberId }
    }

    fun resolveOne(memberPk: Long?): String? {
        if (memberPk == null) return null
        return resolve(listOf(memberPk))[memberPk]
    }
}
