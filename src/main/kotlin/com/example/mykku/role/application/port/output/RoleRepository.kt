package com.example.mykku.role.application.port.output

import com.example.mykku.role.domain.entity.Role
import com.example.mykku.role.domain.vo.RoleId

interface RoleRepository {
    fun save(role: Role): Role
    fun findById(id: RoleId): Role?
    fun findByName(name: String): Role?
    fun findAll(): List<Role>
    fun existsByName(name: String): Boolean
    fun delete(role: Role)
}
