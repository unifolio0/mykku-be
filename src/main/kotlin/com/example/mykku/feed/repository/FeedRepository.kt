package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Feed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FeedRepository : JpaRepository<Feed, UUID> {
}
