package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFeedJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFeedJpaRepository : JpaRepository<SaveFeedJpaEntity, Long> {
    fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean
    fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<SaveFeedJpaEntity>
    fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveFeedJpaEntity>
    fun findByMemberIdAndFolderId(memberId: String, folderId: Long?, pageable: Pageable): Page<SaveFeedJpaEntity>
    fun findByMemberIdAndFeedId(memberId: String, feedId: Long): SaveFeedJpaEntity?
    fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long)
    fun deleteAllByFeedId(feedId: Long)
}
