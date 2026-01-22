package com.example.mykku.block.application.port.output

import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.domain.vo.MemberBlockId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberBlockRepository {
    fun save(memberBlock: MemberBlock): MemberBlock
    fun findById(id: MemberBlockId): MemberBlock?
    fun findByBlockerIdAndBlockedId(blockerId: String, blockedId: String): MemberBlock?
    fun existsByBlockerIdAndBlockedId(blockerId: String, blockedId: String): Boolean
    fun findAllByBlockerId(blockerId: String, pageable: Pageable): Page<MemberBlock>
    fun findBlockedIdsByBlockerId(blockerId: String): List<String>
    fun findBlockerIdsByBlockedId(blockedId: String): List<String>
    fun countByBlockerId(blockerId: String): Long
    fun delete(memberBlock: MemberBlock)
    fun deleteByBlockerIdAndBlockedId(blockerId: String, blockedId: String)
}
