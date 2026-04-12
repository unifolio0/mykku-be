package com.example.mykku.role.adapter.output.persistence

import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.entity.Role
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RoleRepositoryAdapter(
    private val jpaRepository: RoleJpaRepository
) : RoleRepository {

    override fun save(role: Role): Role {
        val entity = if (role.id.value == 0L) {
            RoleJpaEntity.fromDomain(role)
        } else {
            val existingEntity = jpaRepository.findByIdOrNull(role.id.value)
                ?: RoleJpaEntity.fromDomain(role)
            existingEntity.updateFromDomain(role)
            existingEntity
        }
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: RoleId): Role? {
        return jpaRepository.findByIdOrNull(id.value)?.toDomain()
    }

    override fun findByIds(ids: List<RoleId>): List<Role> {
        if (ids.isEmpty()) return emptyList()
        return jpaRepository.findAllById(ids.map { it.value }).map { it.toDomain() }
    }

    override fun findByName(name: String): Role? {
        return jpaRepository.findByName(name)?.toDomain()
    }

    override fun findAll(): List<Role> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun existsByName(name: String): Boolean {
        return jpaRepository.existsByName(name)
    }

    override fun delete(role: Role) {
        jpaRepository.findByIdOrNull(role.id.value)?.let {
            jpaRepository.delete(it)
        }
    }
}
