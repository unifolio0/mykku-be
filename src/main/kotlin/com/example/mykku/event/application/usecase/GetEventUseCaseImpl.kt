package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventDetailResult
import com.example.mykku.event.application.dto.EventImageResult
import com.example.mykku.event.application.port.input.GetEventUseCase
import com.example.mykku.event.application.port.output.EventImageRepository
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.scrap.tool.SaveEventReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetEventUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository,
    private val saveEventReader: SaveEventReader,
    private val memberReader: MemberReader
) : GetEventUseCase {

    @Transactional(readOnly = true)
    override fun execute(eventId: Long, memberId: Long): EventDetailResult {
        val event = eventRepository.findById(EventId.of(eventId))
            ?: throw EventException.eventNotFound()

        val member = memberReader.getMemberById(memberId)
        val images = eventImageRepository.findByEventIds(listOf(event.id))
        val isSaved = saveEventReader.isSavedByEventId(member, eventId)

        return EventDetailResult(
            id = event.id.value,
            title = event.title,
            description = event.description,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            images = images.sortedBy { it.orderIndex }.map {
                EventImageResult(url = it.url, orderIndex = it.orderIndex)
            },
            isSaved = isSaved,
            createdAt = event.createdAt
        )
    }
}
