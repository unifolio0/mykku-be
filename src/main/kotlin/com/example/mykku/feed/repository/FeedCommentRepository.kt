package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeedCommentRepository : JpaRepository<FeedComment, Long> {
    @Query("SELECT fc FROM FeedComment fc JOIN FETCH fc.member WHERE fc.feed = :feed AND fc.parentComment IS NULL ORDER BY fc.createdAt DESC")
    fun findByFeedAndParentCommentIsNull(@Param("feed") feed: Feed, pageable: Pageable): Page<FeedComment>
    
    @Query("SELECT fc FROM FeedComment fc JOIN FETCH fc.member WHERE fc.parentComment = :parentComment ORDER BY fc.createdAt ASC")
    fun findByParentComment(@Param("parentComment") parentComment: FeedComment): List<FeedComment>
    
    fun countByFeed(feed: Feed): Long
}
