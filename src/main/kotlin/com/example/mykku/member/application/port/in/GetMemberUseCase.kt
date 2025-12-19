package com.example.mykku.member.application.port.`in`

import com.example.mykku.member.domain.model.MemberDomain
import com.example.mykku.member.domain.model.MemberId

interface GetMemberUseCase {
    fun execute(id: MemberId): MemberDomain
}