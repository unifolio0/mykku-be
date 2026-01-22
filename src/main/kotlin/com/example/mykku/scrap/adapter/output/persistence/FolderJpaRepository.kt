package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.FolderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FolderJpaRepository : JpaRepository<FolderJpaEntity, Long> {
    fun findByMemberIdAndId(memberId: String, id: Long): FolderJpaEntity?
    fun findByMemberId(memberId: String): List<FolderJpaEntity>
    fun existsByMemberIdAndName(memberId: String, name: String): Boolean
}
