package com.example.mykku.role

import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.tool.MemberRoleReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val memberRoleReader: MemberRoleReader,
    private val memberRepository: MemberRepository
) {
    fun getMyRoles(member: Member): List<MemberRoleResponse> {
        val memberRoles = memberRoleReader.getMemberRolesByMember(member)
        return memberRoles.map { MemberRoleResponse(it, member) }
    }

    @Transactional
    fun changeRepresentativeRole(member: Member, memberRoleId: Long) {
        val memberRole = memberRoleReader.getMemberRoleById(memberRoleId, member)

        member.role = memberRole.role
        memberRepository.save(member)
    }
}
