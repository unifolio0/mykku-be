package com.example.mykku.event.repository

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventImageRepository : JpaRepository<EventImage, Long> {
    fun findByEventIn(events: List<Event>): List<EventImage>
    fun findByEvent(event: Event): List<EventImage>
}
