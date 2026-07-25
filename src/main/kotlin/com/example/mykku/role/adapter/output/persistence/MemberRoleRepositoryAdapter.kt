package com.example.mykku.role.adapter.output.persistence

import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.MemberRoleWithRole
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import com.example.mykku.role.exception.RoleException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MemberRoleRepositoryAdapter(
    private val jpaRepository: MemberRoleJpaRepository,
    private val roleJpaRepository: RoleJpaRepository
) : MemberRoleRepository {

    override fun save(memberRole: MemberRole): MemberRole {
        val roleEntity = roleJpaRepository.findByIdOrNull(memberRole.roleId.value)
            ?: throw RoleException.roleNotFound()

        val entity = MemberRoleJpaEntity.fromDomain(memberRole, roleEntity)
        return jpaRepository.save(entity).toDomain()
    }

    override fun saveIfAbsent(memberId: Long, roleId: RoleId) {
        jpaRepository.insertIfAbsent(memberId, roleId.value)
    }

    override fun findById(id: MemberRoleId): MemberRole? {
        return jpaRepository.findByIdOrNull(id.value)?.toDomain()
    }

    override fun findByMemberId(memberId: Long): List<MemberRole> {
        return jpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun findByMemberIdWithRole(memberId: Long): List<MemberRoleWithRole> {
        return jpaRepository.findByMemberIdWithRole(memberId).map { entity ->
            MemberRoleWithRole(
                memberRole = entity.toDomain(),
                role = entity.role.toDomain()
            )
        }
    }

    override fun findByMemberIdAndRoleId(memberId: Long, roleId: RoleId): MemberRole? {
        return jpaRepository.findByMemberIdAndRoleId(memberId, roleId.value)?.toDomain()
    }

    override fun existsByRoleId(roleId: RoleId): Boolean {
        return jpaRepository.existsByRoleId(roleId.value)
    }

    override fun delete(memberRole: MemberRole) {
        jpaRepository.findByIdOrNull(memberRole.id.value)?.let {
            jpaRepository.delete(it)
        }
    }
}
