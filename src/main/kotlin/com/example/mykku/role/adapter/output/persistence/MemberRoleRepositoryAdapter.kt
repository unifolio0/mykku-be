package com.example.mykku.role.adapter.output.persistence

import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.MemberRoleWithRole
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MemberRoleRepositoryAdapter(
    private val jpaRepository: MemberRoleJpaRepository,
    private val roleJpaRepository: RoleJpaRepository,
    private val memberRepository: MemberRepository
) : MemberRoleRepository {

    override fun save(memberRole: MemberRole): MemberRole {
        val member = memberRepository.findById(memberRole.memberId).orElseThrow {
            IllegalArgumentException("Member not found: ${memberRole.memberId}")
        }

        val roleEntity = roleJpaRepository.findByIdOrNull(memberRole.roleId.value)
            ?: throw IllegalArgumentException("Role not found: ${memberRole.roleId.value}")

        val entity = MemberRoleJpaEntity.fromDomain(memberRole, member, roleEntity)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: MemberRoleId): MemberRole? {
        return jpaRepository.findByIdOrNull(id.value)?.toDomain()
    }

    override fun findByMemberId(memberId: String): List<MemberRole> {
        return jpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun findByMemberIdWithRole(memberId: String): List<MemberRoleWithRole> {
        return jpaRepository.findByMemberIdWithRole(memberId).map { entity ->
            MemberRoleWithRole(
                memberRole = entity.toDomain(),
                role = entity.role.toDomain()
            )
        }
    }

    override fun existsByMemberIdAndRoleId(memberId: String, roleId: RoleId): Boolean {
        return jpaRepository.existsByMemberIdAndRoleId(memberId, roleId.value)
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
