package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.BasicEventRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BasicEventReader(
    private val basicEventRepository: BasicEventRepository,
) {
    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        return basicEventRepository.getByEventPreviews(LocalDateTime.now())
            .map { basicEvent -> EventPreviewResponse(basicEvent) }
            .take(5)
    }
}
