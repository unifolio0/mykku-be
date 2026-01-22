package com.example.mykku.block.application.port.output

import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.vo.KeywordBlockId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface KeywordBlockRepository {
    fun save(keywordBlock: KeywordBlock): KeywordBlock
    fun findById(id: KeywordBlockId): KeywordBlock?
    fun findByMemberIdAndKeyword(memberId: String, keyword: String): KeywordBlock?
    fun existsByMemberIdAndKeyword(memberId: String, keyword: String): Boolean
    fun findAllByMemberId(memberId: String, pageable: Pageable): Page<KeywordBlock>
    fun findKeywordsByMemberId(memberId: String): List<String>
    fun countByMemberId(memberId: String): Long
    fun delete(keywordBlock: KeywordBlock)
    fun deleteByMemberIdAndKeyword(memberId: String, keyword: String)
}
