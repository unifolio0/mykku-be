package com.example.mykku.block.application.port.output

import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.domain.vo.MemberBlockId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberBlockRepository {
    fun save(memberBlock: MemberBlock): MemberBlock
    fun findById(id: MemberBlockId): MemberBlock?
    fun findByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): MemberBlock?
    fun existsByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long): Boolean
    fun findAllByBlockerId(blockerId: Long, pageable: Pageable): Page<MemberBlock>
    fun findBlockedIdsByBlockerId(blockerId: Long): List<Long>
    fun findBlockerIdsByBlockedId(blockedId: Long): List<Long>
    fun countByBlockerId(blockerId: Long): Long
    fun delete(memberBlock: MemberBlock)
    fun deleteByBlockerIdAndBlockedId(blockerId: Long, blockedId: Long)
}
