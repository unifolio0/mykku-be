package com.example.mykku.feed.service

import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.CreateEventResponse
import com.example.mykku.feed.dto.EventImageResponse
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventTagRepository
import com.example.mykku.feed.tool.EventWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EventService(
    private val eventWriter: EventWriter,
    private val eventImageRepository: EventImageRepository,
    private val eventTagRepository: EventTagRepository
) {

    @Transactional
    fun createEvent(request: CreateEventRequest): CreateEventResponse {
        val event = eventWriter.createEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )

        // Fetch the saved images and tags for response
        val eventImages = eventImageRepository.findByEvent(event)
        val eventTags = eventTagRepository.findByEvent(event)

        return CreateEventResponse(
            id = event.id!!,
            title = event.title,
            isContest = event.isContest,
            expiredAt = event.expiredAt,
            images = eventImages
                .sortedBy { it.orderIndex }
                .map { EventImageResponse(url = it.url, orderIndex = it.orderIndex) },
            tags = eventTags.map { it.title },
            createdAt = event.createdAt
        )
    }
}