package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.dto.FanNoteListResult
import com.example.mykku.fannote.application.port.input.GetFanNoteListUseCase
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFanNoteListUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository
) : GetFanNoteListUseCase {

    override fun execute(pageable: Pageable): Page<FanNoteListResult> {
        return fanNoteRepository.findAll(pageable)
            .map { FanNoteListResult.from(it) }
    }
}
