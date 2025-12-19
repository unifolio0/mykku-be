package com.example.mykku.admin.service

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.dto.RoleResponse
import com.example.mykku.role.dto.UpdateRoleRequest
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.application.port.out.MemberRoleQueryPort
import com.example.mykku.role.application.port.out.MemberRoleRepositoryPort
import com.example.mykku.role.application.port.out.RoleQueryPort
import com.example.mykku.role.application.port.out.RoleRepositoryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminRoleService(
    private val roleQueryPort: RoleQueryPort,
    private val roleRepositoryPort: RoleRepositoryPort,
    private val memberRoleRepositoryPort: MemberRoleRepositoryPort,
    private val memberRoleQueryPort: MemberRoleQueryPort,
    private val memberQueryPort: MemberQueryPort
) {
    fun getAllRoles(): List<RoleResponse> {
        val roles = roleQueryPort.getAllRoles()
        return roles.map { RoleResponse(it) }
    }

    @Transactional
    fun createRole(request: CreateRoleRequest): RoleResponse {
        val role = roleRepositoryPort.create(request.name, request.description)
        return RoleResponse(role)
    }

    @Transactional
    fun updateRole(roleId: Long, request: UpdateRoleRequest): RoleResponse {
        val role = roleQueryPort.getRoleById(roleId)
        val updatedRole = roleRepositoryPort.update(role, request.name, request.description)
        return RoleResponse(updatedRole)
    }

    @Transactional
    fun deleteRole(roleId: Long) {
        val role = roleQueryPort.getRoleById(roleId)

        if (memberQueryPort.existsByRoleId(role.id!!)) {
            throw RoleException.roleInUse()
        }

        if (memberRoleQueryPort.existsByRole(role)) {
            throw RoleException.roleInUse()
        }

        roleRepositoryPort.delete(role)
    }

    @Transactional
    fun assignRoleToMember(roleId: Long, memberId: String): MemberRoleResponse {
        val role = roleQueryPort.getRoleById(roleId)
        val member = memberQueryPort.getMemberById(MemberId(memberId))

        val memberRole = memberRoleRepositoryPort.assignRole(member, role)
        return MemberRoleResponse(memberRole, member)
    }
}
