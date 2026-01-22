package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedCommentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedCommentJpaRepository : JpaRepository<LikeFeedCommentJpaEntity, Long> {
    fun existsByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long): Boolean
    fun deleteByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long)
    fun deleteAllByFeedCommentIdIn(feedCommentIds: List<Long>)
}
