package com.example.mykku.admin.service

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.role.adapter.input.web.CreateRoleRequest
import com.example.mykku.role.adapter.input.web.MemberRoleResponse
import com.example.mykku.role.adapter.input.web.RoleResponse
import com.example.mykku.role.adapter.input.web.UpdateRoleRequest
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.exception.RoleException
import com.example.mykku.member.exception.MemberException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminRoleService(
    private val roleJpaRepository: RoleJpaRepository,
    private val memberRoleJpaRepository: MemberRoleJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {
    fun getAllRoles(): List<RoleResponse> {
        val roles = roleJpaRepository.findAll()
        return roles.map { RoleResponse(id = it.id!!, name = it.name, description = it.description) }
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
    fun assignRoleToMember(roleId: Long, memberId: String): MemberRoleResponse {
        val role = roleJpaRepository.findByIdOrNull(roleId)
            ?: throw RoleException.roleNotFound()
        val member = memberJpaRepository.findById(memberId)
            .orElseThrow { MemberException.memberNotFound() }

        val memberRole = MemberRoleJpaEntity(
            member = member,
            role = role
        )
        val savedMemberRole = memberRoleJpaRepository.save(memberRole)
        return MemberRoleResponse(
            id = savedMemberRole.id!!,
            role = RoleResponse(id = role.id!!, name = role.name, description = role.description),
            isRepresentative = member.role?.id == role.id,
            earnedAt = savedMemberRole.createdAt
        )
    }
}
