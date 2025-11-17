package com.example.mykku.role.tool

import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.RoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RoleReader(
    private val roleRepository: RoleRepository
) {
    fun getRoleById(roleId: Long): Role {
        return roleRepository.findByIdOrNull(roleId)
            ?: throw RoleException.roleNotFound()
    }

    fun getRoleByName(name: String): Role {
        return roleRepository.findByName(name)
            ?: throw RoleException.roleNotFound()
    }

    fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    fun existsByName(name: String): Boolean {
        return roleRepository.existsByName(name)
    }
}
