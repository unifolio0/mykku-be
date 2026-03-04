package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFeedJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFeedJpaRepository : JpaRepository<SaveFeedJpaEntity, Long> {
    fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean
    fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<SaveFeedJpaEntity>
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveFeedJpaEntity>
    fun findByMemberIdAndFolderId(memberId: Long, folderId: Long?, pageable: Pageable): Page<SaveFeedJpaEntity>
    fun findByMemberIdAndFeedId(memberId: Long, feedId: Long): SaveFeedJpaEntity?
    fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long)
    fun deleteAllByFeedId(feedId: Long)
}
