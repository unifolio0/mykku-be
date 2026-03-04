package com.example.mykku.block.adapter.output.persistence.repository

import com.example.mykku.block.adapter.output.persistence.entity.KeywordBlockJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface KeywordBlockJpaRepository : JpaRepository<KeywordBlockJpaEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(kb) > 0 THEN true ELSE false END FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId AND kb.keyword = :keyword")
    fun existsByMemberIdAndKeyword(@Param("memberId") memberId: Long, @Param("keyword") keyword: String): Boolean

    @Query("SELECT kb FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId AND kb.keyword = :keyword")
    fun findByMemberIdAndKeyword(@Param("memberId") memberId: Long, @Param("keyword") keyword: String): KeywordBlockJpaEntity?

    @Query("SELECT kb.keyword FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId")
    fun findKeywordsByMemberId(@Param("memberId") memberId: Long): List<String>

    @Query("SELECT kb FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId")
    fun findAllByMemberId(@Param("memberId") memberId: Long, pageable: Pageable): Page<KeywordBlockJpaEntity>

    @Modifying
    @Query("DELETE FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId AND kb.keyword = :keyword")
    fun deleteByMemberIdAndKeyword(@Param("memberId") memberId: Long, @Param("keyword") keyword: String)

    @Query("SELECT COUNT(kb) FROM KeywordBlockJpaEntity kb WHERE kb.member.id = :memberId")
    fun countByMemberId(@Param("memberId") memberId: Long): Long
}
