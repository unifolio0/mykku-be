package com.example.mykku.block.adapter.output.persistence.repository

import com.example.mykku.block.adapter.output.persistence.entity.MemberBlockJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberBlockJpaRepository : JpaRepository<MemberBlockJpaEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(mb) > 0 THEN true ELSE false END FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId AND mb.blocked.id = :blockedId")
    fun existsByBlockerIdAndBlockedId(@Param("blockerId") blockerId: Long, @Param("blockedId") blockedId: Long): Boolean

    @Query("SELECT mb FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId AND mb.blocked.id = :blockedId")
    fun findByBlockerIdAndBlockedId(@Param("blockerId") blockerId: Long, @Param("blockedId") blockedId: Long): MemberBlockJpaEntity?

    @Query("SELECT mb.blocked.id FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId")
    fun findBlockedIdsByBlockerId(@Param("blockerId") blockerId: Long): List<Long>

    @Query("SELECT mb.blocker.id FROM MemberBlockJpaEntity mb WHERE mb.blocked.id = :blockedId")
    fun findBlockerIdsByBlockedId(@Param("blockedId") blockedId: Long): List<Long>

    @Query("SELECT mb FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId")
    fun findAllByBlockerId(@Param("blockerId") blockerId: Long, pageable: Pageable): Page<MemberBlockJpaEntity>

    @Modifying
    @Query("DELETE FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId AND mb.blocked.id = :blockedId")
    fun deleteByBlockerIdAndBlockedId(@Param("blockerId") blockerId: Long, @Param("blockedId") blockedId: Long)

    @Query("SELECT COUNT(mb) FROM MemberBlockJpaEntity mb WHERE mb.blocker.id = :blockerId")
    fun countByBlockerId(@Param("blockerId") blockerId: Long): Long
}
