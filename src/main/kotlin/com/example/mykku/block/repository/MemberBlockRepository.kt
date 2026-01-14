package com.example.mykku.block.repository

import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberBlockRepository : JpaRepository<MemberBlock, Long> {

    fun existsByBlockerAndBlocked(blocker: Member, blocked: Member): Boolean

    fun findByBlockerAndBlocked(blocker: Member, blocked: Member): MemberBlock?

    @Query("SELECT mb.blocked.id FROM MemberBlock mb WHERE mb.blocker.id = :blockerId")
    fun findBlockedMemberIdsByBlockerId(@Param("blockerId") blockerId: String): List<String>

    @Query("SELECT mb.blocker.id FROM MemberBlock mb WHERE mb.blocked.id = :blockedId")
    fun findBlockerMemberIdsByBlockedId(@Param("blockedId") blockedId: String): List<String>

    fun findAllByBlocker(blocker: Member, pageable: Pageable): Page<MemberBlock>

    fun deleteByBlockerAndBlocked(blocker: Member, blocked: Member)

    fun countByBlocker(blocker: Member): Long
}
