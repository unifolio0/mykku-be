package com.example.mykku.role.adapter.output.persistence.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRoleJpaRepository : JpaRepository<MemberRoleJpaEntity, Long> {
    fun findByMember(member: Member): List<MemberRoleJpaEntity>
    fun findByMemberId(memberId: String): List<MemberRoleJpaEntity>
    fun findByMemberAndRole(member: Member, role: RoleJpaEntity): MemberRoleJpaEntity?
    fun existsByMemberAndRole(member: Member, role: RoleJpaEntity): Boolean
    fun existsByMemberIdAndRoleId(memberId: String, roleId: Long): Boolean
    fun existsByRole(role: RoleJpaEntity): Boolean
    fun existsByRoleId(roleId: Long): Boolean

    @Query("SELECT mr FROM MemberRoleJpaEntity mr JOIN FETCH mr.role WHERE mr.member = :member")
    fun findByMemberWithRole(member: Member): List<MemberRoleJpaEntity>

    @Query("SELECT mr FROM MemberRoleJpaEntity mr JOIN FETCH mr.role WHERE mr.member.id = :memberId")
    fun findByMemberIdWithRole(memberId: String): List<MemberRoleJpaEntity>
}
