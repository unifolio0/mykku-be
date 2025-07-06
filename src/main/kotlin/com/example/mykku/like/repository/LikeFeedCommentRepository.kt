package com.example.mykku.like.repository

import com.example.mykku.like.domain.LikeFeedComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedCommentRepository : JpaRepository<LikeFeedComment, Long> {
    fun existsByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long): Boolean

    fun deleteByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long)
}
