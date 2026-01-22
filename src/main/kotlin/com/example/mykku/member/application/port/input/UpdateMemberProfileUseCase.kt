package com.example.mykku.member.application.port.input

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.UpdateProfileCommand
import com.example.mykku.member.domain.entity.Member

interface UpdateMemberProfileUseCase {
    fun updateProfile(member: Member, command: UpdateProfileCommand): MemberProfileResult
}
