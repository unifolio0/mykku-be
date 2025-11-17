package com.example.mykku.role.tool

import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.RoleRepository
import org.springframework.stereotype.Component

@Component
class RoleWriter(
    private val roleRepository: RoleRepository
) {
    fun save(role: Role): Role {
        return roleRepository.save(role)
    }

    fun create(name: String, description: String?): Role {
        if (roleRepository.existsByName(name)) {
            throw RoleException.roleNameDuplicate()
        }

        val role = Role(
            name = name,
            description = description
        )
        return roleRepository.save(role)
    }

    fun update(role: Role, name: String, description: String?): Role {
        if (role.name != name && roleRepository.existsByName(name)) {
            throw RoleException.roleNameDuplicate()
        }

        role.name = name
        role.description = description
        return roleRepository.save(role)
    }

    fun delete(role: Role) {
        roleRepository.delete(role)
    }
}
