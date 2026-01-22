package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.port.input.GetMemberProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleJpaRepository: RoleJpaRepository
) : GetMemberProfileUseCase {

    override fun getMyProfile(member: Member): MemberProfileResult {
        val roleName = member.roleId?.let { roleJpaRepository.findByIdOrNull(it)?.name }
        return MemberProfileResult.from(member, roleName)
    }
}
