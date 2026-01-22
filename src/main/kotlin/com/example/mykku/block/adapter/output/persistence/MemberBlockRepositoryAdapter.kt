package com.example.mykku.block.adapter.output.persistence

import com.example.mykku.block.adapter.output.persistence.entity.MemberBlockJpaEntity
import com.example.mykku.block.adapter.output.persistence.repository.MemberBlockJpaRepository
import com.example.mykku.block.application.port.output.MemberBlockRepository
import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.domain.vo.MemberBlockId
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MemberBlockRepositoryAdapter(
    private val memberBlockJpaRepository: MemberBlockJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : MemberBlockRepository {

    override fun save(memberBlock: MemberBlock): MemberBlock {
        val blockerEntity = memberJpaRepository.findById(memberBlock.blockerId)
            .orElseThrow { IllegalArgumentException("Blocker not found: ${memberBlock.blockerId}") }

        val blockedEntity = memberJpaRepository.findById(memberBlock.blockedId)
            .orElseThrow { IllegalArgumentException("Blocked member not found: ${memberBlock.blockedId}") }

        val entity = MemberBlockJpaEntity.fromDomain(memberBlock, blockerEntity, blockedEntity)
        return memberBlockJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: MemberBlockId): MemberBlock? {
        return memberBlockJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByBlockerIdAndBlockedId(blockerId: String, blockedId: String): MemberBlock? {
        return memberBlockJpaRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)?.toDomain()
    }

    override fun existsByBlockerIdAndBlockedId(blockerId: String, blockedId: String): Boolean {
        return memberBlockJpaRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)
    }

    override fun findAllByBlockerId(blockerId: String, pageable: Pageable): Page<MemberBlock> {
        return memberBlockJpaRepository.findAllByBlockerId(blockerId, pageable)
            .map { it.toDomain() }
    }

    override fun findBlockedIdsByBlockerId(blockerId: String): List<String> {
        return memberBlockJpaRepository.findBlockedIdsByBlockerId(blockerId)
    }

    override fun findBlockerIdsByBlockedId(blockedId: String): List<String> {
        return memberBlockJpaRepository.findBlockerIdsByBlockedId(blockedId)
    }

    override fun countByBlockerId(blockerId: String): Long {
        return memberBlockJpaRepository.countByBlockerId(blockerId)
    }

    override fun delete(memberBlock: MemberBlock) {
        memberBlock.id?.let { id ->
            memberBlockJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteByBlockerIdAndBlockedId(blockerId: String, blockedId: String) {
        memberBlockJpaRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId)
    }
}
