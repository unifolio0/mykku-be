package com.example.mykku.role.adapter.output.persistence.repository

import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MemberRoleJpaRepository : JpaRepository<MemberRoleJpaEntity, Long> {
    fun findByMemberId(memberId: Long): List<MemberRoleJpaEntity>
    fun findByMemberIdAndRoleId(memberId: Long, roleId: Long): MemberRoleJpaEntity?
    fun existsByRole(role: RoleJpaEntity): Boolean
    fun existsByRoleId(roleId: Long): Boolean

    @Query("SELECT mr FROM MemberRoleJpaEntity mr JOIN FETCH mr.role WHERE mr.memberId = :memberId")
    fun findByMemberIdWithRole(memberId: Long): List<MemberRoleJpaEntity>

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
        value = """
            INSERT INTO member_role (member_id, role_id, created_at, updated_at)
            VALUES (:memberId, :roleId, NOW(6), NOW(6))
            ON DUPLICATE KEY UPDATE updated_at = NOW(6)
        """,
        nativeQuery = true
    )
    fun insertIfAbsent(memberId: Long, roleId: Long): Int

    @Query(
        """
        SELECT mr FROM MemberRoleJpaEntity mr JOIN FETCH mr.role
        WHERE mr.memberId = :memberId AND mr.checkedAt IS NULL
        ORDER BY mr.id
        """
    )
    fun findUncheckedByMemberIdWithRole(@Param("memberId") memberId: Long): List<MemberRoleJpaEntity>

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
        """
        UPDATE MemberRoleJpaEntity mr SET mr.checkedAt = :now, mr.updatedAt = :now
        WHERE mr.id = :id AND mr.checkedAt IS NULL
        """
    )
    fun markChecked(@Param("id") id: Long, @Param("now") now: LocalDateTime): Int
}
