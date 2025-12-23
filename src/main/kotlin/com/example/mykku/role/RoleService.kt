package com.example.mykku.role

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.application.port.out.MemberRoleQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val memberRoleQueryPort: MemberRoleQueryPort,
    private val memberQueryPort: MemberQueryPort
) {
    fun getMyRoles(member: Member): List<MemberRoleResponse> {
        val memberRoles = memberRoleQueryPort.getMemberRolesByMember(member)
        return memberRoles.map { MemberRoleResponse(it, member) }
    }

    @Transactional
    fun changeRepresentativeRole(member: Member, memberRoleId: Long) {
        val memberRole = memberRoleQueryPort.getMemberRoleById(memberRoleId, member)

        member.role = memberRole.role
        memberQueryPort.saveMember(member)
    }
}
