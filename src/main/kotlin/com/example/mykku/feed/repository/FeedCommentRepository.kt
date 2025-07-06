package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.FeedComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedCommentRepository : JpaRepository<FeedComment, Long> {
}
