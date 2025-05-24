package com.example.mykku.feed.service

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.TagRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TagService(
    private val tagRepository: TagRepository,
) {
    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        return tagRepository.getByEventPreviews(LocalDateTime.now())
            .map { tag -> EventPreviewResponse(tag) }
            .take(5)
    }
}
