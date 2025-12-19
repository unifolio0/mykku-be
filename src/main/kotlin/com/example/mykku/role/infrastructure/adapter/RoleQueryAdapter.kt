package com.example.mykku.role.infrastructure.adapter

import com.example.mykku.role.application.port.out.RoleQueryPort
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.RoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RoleQueryAdapter(
    private val roleRepository: RoleRepository
) : RoleQueryPort {

    override fun getRoleById(roleId: Long): Role {
        return roleRepository.findByIdOrNull(roleId)
            ?: throw RoleException.roleNotFound()
    }

    override fun getRoleByName(name: String): Role {
        return roleRepository.findByName(name)
            ?: throw RoleException.roleNotFound()
    }

    override fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    override fun existsByName(name: String): Boolean {
        return roleRepository.existsByName(name)
    }
}
