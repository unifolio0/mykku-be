package com.example.mykku.member.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, String> {
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Member?
    fun existsByRole(role: Role): Boolean
    fun existsByRoleId(roleId: Long): Boolean
}
