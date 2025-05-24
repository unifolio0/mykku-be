package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Feed
import org.springframework.data.jpa.repository.JpaRepository

interface FeedRepository : JpaRepository<Feed, Long> {
}
