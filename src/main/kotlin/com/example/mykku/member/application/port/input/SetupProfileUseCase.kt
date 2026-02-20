package com.example.mykku.member.application.port.input

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.SetupProfileCommand
import com.example.mykku.member.domain.entity.Member

interface SetupProfileUseCase {
    fun setupProfile(member: Member, command: SetupProfileCommand): MemberProfileResult
}
