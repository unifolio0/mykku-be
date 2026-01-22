package com.example.mykku.block.adapter.output.persistence

import com.example.mykku.block.adapter.output.persistence.entity.KeywordBlockJpaEntity
import com.example.mykku.block.adapter.output.persistence.repository.KeywordBlockJpaRepository
import com.example.mykku.block.application.port.output.KeywordBlockRepository
import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.vo.KeywordBlockId
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class KeywordBlockRepositoryAdapter(
    private val keywordBlockJpaRepository: KeywordBlockJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : KeywordBlockRepository {

    override fun save(keywordBlock: KeywordBlock): KeywordBlock {
        val memberEntity = memberJpaRepository.findById(keywordBlock.memberId)
            .orElseThrow { IllegalArgumentException("Member not found: ${keywordBlock.memberId}") }

        val entity = KeywordBlockJpaEntity.fromDomain(keywordBlock, memberEntity)
        return keywordBlockJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: KeywordBlockId): KeywordBlock? {
        return keywordBlockJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByMemberIdAndKeyword(memberId: String, keyword: String): KeywordBlock? {
        return keywordBlockJpaRepository.findByMemberIdAndKeyword(memberId, keyword)?.toDomain()
    }

    override fun existsByMemberIdAndKeyword(memberId: String, keyword: String): Boolean {
        return keywordBlockJpaRepository.existsByMemberIdAndKeyword(memberId, keyword)
    }

    override fun findAllByMemberId(memberId: String, pageable: Pageable): Page<KeywordBlock> {
        return keywordBlockJpaRepository.findAllByMemberId(memberId, pageable)
            .map { it.toDomain() }
    }

    override fun findKeywordsByMemberId(memberId: String): List<String> {
        return keywordBlockJpaRepository.findKeywordsByMemberId(memberId)
    }

    override fun countByMemberId(memberId: String): Long {
        return keywordBlockJpaRepository.countByMemberId(memberId)
    }

    override fun delete(keywordBlock: KeywordBlock) {
        keywordBlock.id?.let { id ->
            keywordBlockJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteByMemberIdAndKeyword(memberId: String, keyword: String) {
        keywordBlockJpaRepository.deleteByMemberIdAndKeyword(memberId, keyword)
    }
}
