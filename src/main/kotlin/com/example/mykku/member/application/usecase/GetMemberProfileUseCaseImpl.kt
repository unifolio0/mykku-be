package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.port.input.GetMemberProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository
) : GetMemberProfileUseCase {

    override fun getMyProfile(member: Member): MemberProfileResult {
        val role = member.roleId?.let { roleRepository.findById(RoleId.of(it)) }
        val roleResult = role?.let { RoleResult(it.id.value, it.name, it.description) }
        return MemberProfileResult.from(member, roleResult)
    }
}
