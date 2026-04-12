package com.example.mykku.member.application.port.output

import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.member.domain.vo.SocialProvider

interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: MemberPk): Member?
    fun findByIds(ids: List<MemberPk>): List<Member>
    fun findByProviderAndSocialId(provider: SocialProvider, socialId: String): Member?
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Member?
    fun existsByMemberId(memberId: String): Boolean
    fun findByMemberId(memberId: String): Member?
    fun deleteById(id: MemberPk)
}
