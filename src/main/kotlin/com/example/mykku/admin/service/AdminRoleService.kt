package com.example.mykku.admin.service

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.dto.RoleResponse
import com.example.mykku.role.dto.UpdateRoleRequest
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.tool.MemberRoleReader
import com.example.mykku.role.tool.MemberRoleWriter
import com.example.mykku.role.tool.RoleReader
import com.example.mykku.role.tool.RoleWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminRoleService(
    private val roleReader: RoleReader,
    private val roleWriter: RoleWriter,
    private val memberRoleWriter: MemberRoleWriter,
    private val memberRoleReader: MemberRoleReader,
    private val memberQueryPort: MemberQueryPort
) {
    fun getAllRoles(): List<RoleResponse> {
        val roles = roleReader.getAllRoles()
        return roles.map { RoleResponse(it) }
    }

    @Transactional
    fun createRole(request: CreateRoleRequest): RoleResponse {
        val role = roleWriter.create(request.name, request.description)
        return RoleResponse(role)
    }

    @Transactional
    fun updateRole(roleId: Long, request: UpdateRoleRequest): RoleResponse {
        val role = roleReader.getRoleById(roleId)
        val updatedRole = roleWriter.update(role, request.name, request.description)
        return RoleResponse(updatedRole)
    }

    @Transactional
    fun deleteRole(roleId: Long) {
        val role = roleReader.getRoleById(roleId)

        if (memberQueryPort.existsByRoleId(role.id!!)) {
            throw RoleException.roleInUse()
        }

        if (memberRoleReader.existsByRole(role)) {
            throw RoleException.roleInUse()
        }

        roleWriter.delete(role)
    }

    @Transactional
    fun assignRoleToMember(roleId: Long, memberId: String): MemberRoleResponse {
        val role = roleReader.getRoleById(roleId)
        val member = memberQueryPort.getMemberById(MemberId(memberId))

        val memberRole = memberRoleWriter.assignRole(member, role)
        return MemberRoleResponse(memberRole, member)
    }
}
