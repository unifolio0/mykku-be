package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.port.input.GetFanNoteDetailUseCase
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.exception.FanNoteException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFanNoteDetailUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) : GetFanNoteDetailUseCase {

    override fun execute(fanNoteId: Long): FanNoteDetailResult {
        val fanNote = fanNoteRepository.findById(FanNoteId.of(fanNoteId))
            ?: throw FanNoteException.fanNoteNotFound()

        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(FanNoteId.of(fanNoteId))

        return FanNoteDetailResult.from(fanNote, pages)
    }
}
