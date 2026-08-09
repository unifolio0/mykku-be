package com.example.mykku.role.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "member_role")
class MemberRoleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: RoleJpaEntity,

    @Column(name = "checked_at")
    var checkedAt: LocalDateTime? = null
) : BaseJpaEntity() {

    fun toDomain(): MemberRole {
        return MemberRole.reconstitute(
            id = MemberRoleId.of(id!!),
            memberId = memberId,
            roleId = RoleId.of(role.id!!),
            checkedAt = checkedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: MemberRole, role: RoleJpaEntity): MemberRoleJpaEntity {
            return MemberRoleJpaEntity(
                id = if (domain.id.value == 0L) null else domain.id.value,
                memberId = domain.memberId,
                role = role,
                checkedAt = domain.checkedAt
            )
        }
    }
}
