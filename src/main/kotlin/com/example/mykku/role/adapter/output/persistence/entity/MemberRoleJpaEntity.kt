package com.example.mykku.role.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member_role")
class MemberRoleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: RoleJpaEntity
) : BaseEntity() {

    fun toDomain(): MemberRole {
        return MemberRole.reconstitute(
            id = MemberRoleId.of(id!!),
            memberId = member.id,
            roleId = RoleId.of(role.id!!),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: MemberRole, member: Member, role: RoleJpaEntity): MemberRoleJpaEntity {
            return MemberRoleJpaEntity(
                id = if (domain.id.value == 0L) null else domain.id.value,
                member = member,
                role = role
            )
        }
    }
}
