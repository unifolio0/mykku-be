package com.example.mykku.block.application.port.output

import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.vo.KeywordBlockId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface KeywordBlockRepository {
    fun save(keywordBlock: KeywordBlock): KeywordBlock
    fun findById(id: KeywordBlockId): KeywordBlock?
    fun findByMemberIdAndKeyword(memberId: Long, keyword: String): KeywordBlock?
    fun existsByMemberIdAndKeyword(memberId: Long, keyword: String): Boolean
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<KeywordBlock>
    fun findKeywordsByMemberId(memberId: Long): List<String>
    fun countByMemberId(memberId: Long): Long
    fun delete(keywordBlock: KeywordBlock)
    fun deleteByMemberIdAndKeyword(memberId: Long, keyword: String)
}
