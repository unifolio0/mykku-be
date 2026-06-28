package com.example.mykku.member.application.port.input

import com.example.mykku.member.application.dto.ChangeMemberIdCommand
import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.domain.entity.Member

interface ChangeMemberIdUseCase {
    fun changeMemberId(member: Member, command: ChangeMemberIdCommand): MemberProfileResult
}
