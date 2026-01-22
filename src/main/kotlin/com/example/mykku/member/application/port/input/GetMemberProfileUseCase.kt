package com.example.mykku.member.application.port.input

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.domain.entity.Member

interface GetMemberProfileUseCase {
    fun getMyProfile(member: Member): MemberProfileResult
}
