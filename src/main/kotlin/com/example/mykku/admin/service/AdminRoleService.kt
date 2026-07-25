package com.example.mykku.admin.service

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.role.adapter.input.web.CreateRoleRequest
import com.example.mykku.role.adapter.input.web.MemberRoleResponse
import com.example.mykku.role.adapter.input.web.RoleResponse
import com.example.mykku.role.adapter.input.web.UpdateRoleRequest
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.application.dto.AcquireRoleCommand
import com.example.mykku.role.application.port.input.AcquireRoleUseCase
import com.example.mykku.role.application.port.input.GetRolesUseCase
import com.example.mykku.role.exception.RoleException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminRoleService(
    private val roleJpaRepository: RoleJpaRepository,
    private val memberRoleJpaRepository: MemberRoleJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val acquireRoleUseCase: AcquireRoleUseCase,
    private val getRolesUseCase: GetRolesUseCase
) {
    fun getAllRoles(): List<RoleResponse> {
        return getRolesUseCase.getRoles().map { RoleResponse.from(it) }
    }

    @Transactional
    fun createRole(request: CreateRoleRequest): RoleResponse {
        val role = com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity(
            name = request.name,
            description = request.description
        )
        val savedRole = roleJpaRepository.save(role)
        return RoleResponse(id = savedRole.id!!, name = savedRole.name, description = savedRole.description)
    }

    @Transactional
    fun updateRole(roleId: Long, request: UpdateRoleRequest): RoleResponse {
        val role = roleJpaRepository.findByIdOrNull(roleId)
            ?: throw RoleException.roleNotFound()
        role.name = request.name
        role.description = request.description
        val savedRole = roleJpaRepository.save(role)
        return RoleResponse(id = savedRole.id!!, name = savedRole.name, description = savedRole.description)
    }

    @Transactional
    fun deleteRole(roleId: Long) {
        val role = roleJpaRepository.findByIdOrNull(roleId)
            ?: throw RoleException.roleNotFound()

        if (memberJpaRepository.existsByRole(role)) {
            throw RoleException.roleInUse()
        }

        if (memberRoleJpaRepository.existsByRole(role)) {
            throw RoleException.roleInUse()
        }

        roleJpaRepository.delete(role)
    }

    @Transactional
    fun assignRoleToMember(roleId: Long, memberId: Long): MemberRoleResponse {
        val result = acquireRoleUseCase.acquireRole(AcquireRoleCommand(memberId = memberId, roleId = roleId))
        return MemberRoleResponse.from(result.memberRole)
    }
}
