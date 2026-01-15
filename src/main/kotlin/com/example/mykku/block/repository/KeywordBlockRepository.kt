package com.example.mykku.block.repository

import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface KeywordBlockRepository : JpaRepository<KeywordBlock, Long> {

    fun existsByMemberAndKeyword(member: Member, keyword: String): Boolean

    fun findByMemberAndKeyword(member: Member, keyword: String): KeywordBlock?

    @Query("SELECT kb.keyword FROM KeywordBlock kb WHERE kb.member.id = :memberId")
    fun findKeywordsByMemberId(@Param("memberId") memberId: String): List<String>

    fun findAllByMember(member: Member, pageable: Pageable): Page<KeywordBlock>

    fun deleteByMemberAndKeyword(member: Member, keyword: String)

    fun countByMember(member: Member): Long
}
