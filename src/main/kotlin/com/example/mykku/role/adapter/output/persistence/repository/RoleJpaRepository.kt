package com.example.mykku.role.adapter.output.persistence.repository

import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleJpaRepository : JpaRepository<RoleJpaEntity, Long> {
    fun findByName(name: String): RoleJpaEntity?
    fun existsByName(name: String): Boolean
}
