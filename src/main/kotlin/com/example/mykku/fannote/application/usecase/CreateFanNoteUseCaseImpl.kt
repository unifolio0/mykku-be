package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.dto.CreateFanNoteCommand
import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.port.input.CreateFanNoteUseCase
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.entity.FanNotePage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateFanNoteUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) : CreateFanNoteUseCase {

    override fun execute(command: CreateFanNoteCommand): FanNoteDetailResult {
        val fanNote = FanNote.create(
            title = command.title,
            subtitle = command.subtitle,
            content = command.content,
            productionDate = command.productionDate,
            coverImageUrl = command.coverImageUrl
        )

        val savedFanNote = fanNoteRepository.save(fanNote)

        val pages = command.pageImageUrls.mapIndexed { index, imageUrl ->
            FanNotePage.create(
                fanNoteId = savedFanNote.id.value,
                pageNumber = index + 1,
                imageUrl = imageUrl
            )
        }

        val savedPages = if (pages.isNotEmpty()) {
            fanNotePageRepository.saveAll(pages)
        } else {
            emptyList()
        }

        return FanNoteDetailResult.from(savedFanNote, savedPages)
    }
}
