package com.example.mykku.role.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.role.domain.entity.Role
import com.example.mykku.role.domain.vo.RoleId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "role")
class RoleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(length = 200)
    var description: String? = null
) : BaseJpaEntity() {

    fun toDomain(): Role {
        return Role.reconstitute(
            id = RoleId.of(id!!),
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(domain: Role) {
        this.name = domain.name
        this.description = domain.description
    }

    companion object {
        fun fromDomain(domain: Role): RoleJpaEntity {
            return RoleJpaEntity(
                id = if (domain.id.value == 0L) null else domain.id.value,
                name = domain.name,
                description = domain.description
            )
        }
    }
}
