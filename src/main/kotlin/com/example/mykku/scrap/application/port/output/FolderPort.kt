package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.domain.entity.FolderEntity

interface FolderPort {
    fun save(folder: FolderEntity): FolderEntity
    fun findByMemberIdAndId(memberId: String, id: Long): FolderEntity?
    fun findByMemberId(memberId: String): List<FolderEntity>
    fun existsByMemberIdAndName(memberId: String, name: String): Boolean
    fun delete(folder: FolderEntity)
}
