package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.port.input.GetMemberProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.tool.RoleReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleReader: RoleReader
) : GetMemberProfileUseCase {

    override fun getMyProfile(member: Member): MemberProfileResult {
        val roleName = member.roleId?.let { roleReader.findById(it)?.name }
        return MemberProfileResult.from(member, roleName)
    }
}
