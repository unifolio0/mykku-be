package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.domain.entity.FolderEntity

interface FolderPort {
    fun save(folder: FolderEntity): FolderEntity
    fun findByMemberIdAndId(memberId: Long, id: Long): FolderEntity?
    fun findByMemberId(memberId: Long): List<FolderEntity>
    fun existsByMemberIdAndName(memberId: Long, name: String): Boolean
    fun delete(folder: FolderEntity)
}
