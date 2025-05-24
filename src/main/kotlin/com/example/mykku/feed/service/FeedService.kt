package com.example.mykku.feed.service

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.TagRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FeedService(
    private val tagRepository: TagRepository,
) {
    fun getProcessingEvents(): List<EventPreviewResponse> {
        return tagRepository.getByEventPreviews(LocalDateTime.now())
            .map { tag -> EventPreviewResponse(tag) }
            .take(5)
    }
}
