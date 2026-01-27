package com.example.mykku.role.adapter.output.persistence.repository

import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRoleJpaRepository : JpaRepository<MemberRoleJpaEntity, Long> {
    fun findByMemberId(memberId: String): List<MemberRoleJpaEntity>
    fun existsByMemberIdAndRoleId(memberId: String, roleId: Long): Boolean
    fun existsByRole(role: RoleJpaEntity): Boolean
    fun existsByRoleId(roleId: Long): Boolean

    @Query("SELECT mr FROM MemberRoleJpaEntity mr JOIN FETCH mr.role WHERE mr.memberId = :memberId")
    fun findByMemberIdWithRole(memberId: String): List<MemberRoleJpaEntity>
}
