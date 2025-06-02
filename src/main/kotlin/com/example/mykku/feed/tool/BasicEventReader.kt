package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.BasicEventRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BasicEventReader(
    private val basicEventRepository: BasicEventRepository,
) {
    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        return basicEventRepository.getByEventPreviews(LocalDateTime.now())
            .map { basicEvent -> EventPreviewResponse(basicEvent) }
            .take(5)
    }
}
