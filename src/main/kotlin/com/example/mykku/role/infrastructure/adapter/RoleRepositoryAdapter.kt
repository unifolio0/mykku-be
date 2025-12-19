package com.example.mykku.role.infrastructure.adapter

import com.example.mykku.role.application.port.out.RoleQueryPort
import com.example.mykku.role.application.port.out.RoleRepositoryPort
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.RoleRepository
import org.springframework.stereotype.Component

@Component
class RoleRepositoryAdapter(
    private val roleRepository: RoleRepository,
    private val roleQueryPort: RoleQueryPort
) : RoleRepositoryPort {

    override fun save(role: Role): Role {
        return roleRepository.save(role)
    }

    override fun create(name: String, description: String?): Role {
        if (roleQueryPort.existsByName(name)) {
            throw RoleException.roleNameDuplicate()
        }

        val role = Role(
            name = name,
            description = description
        )
        return roleRepository.save(role)
    }

    override fun update(role: Role, name: String, description: String?): Role {
        if (role.name != name && roleQueryPort.existsByName(name)) {
            throw RoleException.roleNameDuplicate()
        }

        role.name = name
        role.description = description
        return roleRepository.save(role)
    }

    override fun delete(role: Role) {
        roleRepository.delete(role)
    }
}
