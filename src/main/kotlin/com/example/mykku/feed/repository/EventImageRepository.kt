package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventImageRepository : JpaRepository<EventImage, Long> {
    fun findByEvent(event: Event): List<EventImage>
    fun findByEventIn(events: List<Event>): List<EventImage>
}