package com.example.mykku.role.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRoleRepository : JpaRepository<MemberRole, Long> {
    fun findByMember(member: Member): List<MemberRole>
    fun findByMemberAndRole(member: Member, role: Role): MemberRole?
    fun existsByMemberAndRole(member: Member, role: Role): Boolean
    fun existsByRole(role: Role): Boolean

    @Query("SELECT mr FROM MemberRole mr JOIN FETCH mr.role WHERE mr.member = :member")
    fun findByMemberWithRole(member: Member): List<MemberRole>
}
