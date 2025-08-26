package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.EventTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventTagRepository : JpaRepository<EventTag, Long> {
    fun findAllByTitleIn(titles: Collection<String>): List<EventTag>
}
