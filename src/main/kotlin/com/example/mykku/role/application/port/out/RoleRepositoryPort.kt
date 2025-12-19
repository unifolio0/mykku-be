package com.example.mykku.role.application.port.out

import com.example.mykku.role.domain.Role

interface RoleRepositoryPort {
    fun save(role: Role): Role
    fun create(name: String, description: String?): Role
    fun update(role: Role, name: String, description: String?): Role
    fun delete(role: Role)
}
