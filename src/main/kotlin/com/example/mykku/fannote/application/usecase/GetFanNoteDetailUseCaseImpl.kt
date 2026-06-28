package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.port.input.GetFanNoteDetailUseCase
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.fannote.exception.FanNoteException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFanNoteDetailUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository,
    private val eventPublisher: ApplicationEventPublisher
) : GetFanNoteDetailUseCase {

    override fun execute(fanNoteId: Long, memberId: Long?): FanNoteDetailResult {
        val fanNote = fanNoteRepository.findById(FanNoteId.of(fanNoteId))
            ?: throw FanNoteException.fanNoteNotFound()

        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(FanNoteId.of(fanNoteId))

        memberId?.let { eventPublisher.publishEvent(ActivityEvent(it, ActivityType.FANNOTE_VIEW)) }

        return FanNoteDetailResult.from(fanNote, pages)
    }
}
