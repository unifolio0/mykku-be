package com.example.mykku.member.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.role.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberJpaRepository : JpaRepository<MemberJpaEntity, String> {
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): MemberJpaEntity?
    fun existsByRole(role: Role): Boolean
    fun existsByMemberId(memberId: String): Boolean
    fun findByMemberId(memberId: String): MemberJpaEntity?
}
