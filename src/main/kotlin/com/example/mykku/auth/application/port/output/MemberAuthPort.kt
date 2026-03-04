package com.example.mykku.auth.application.port.output

import com.example.mykku.auth.application.dto.OAuthMemberInfo
import com.example.mykku.member.domain.entity.Member

interface MemberAuthPort {
    fun findOrCreate(memberInfo: OAuthMemberInfo): Pair<Member, Boolean>
    fun findById(memberId: Long): Member?
}
