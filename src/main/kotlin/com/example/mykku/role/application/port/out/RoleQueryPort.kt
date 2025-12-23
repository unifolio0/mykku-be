package com.example.mykku.role.application.port.out

import com.example.mykku.role.domain.Role

interface RoleQueryPort {
    fun getRoleById(roleId: Long): Role
    fun getRoleByName(name: String): Role
    fun getAllRoles(): List<Role>
    fun existsByName(name: String): Boolean
}
