package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.dto.UpdateFanNoteCommand
import com.example.mykku.fannote.application.port.input.UpdateFanNoteUseCase
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.entity.FanNotePage
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.exception.FanNoteException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateFanNoteUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) : UpdateFanNoteUseCase {

    override fun execute(command: UpdateFanNoteCommand): FanNoteDetailResult {
        val fanNoteId = FanNoteId.of(command.fanNoteId)
        val fanNote = fanNoteRepository.findById(fanNoteId)
            ?: throw FanNoteException.fanNoteNotFound()

        val updatedFanNote = fanNote.updateInfo(
            title = command.title,
            subtitle = command.subtitle,
            content = command.content,
            productionDate = command.productionDate,
            coverImageUrl = command.coverImageUrl
        )

        val savedFanNote = fanNoteRepository.save(updatedFanNote)

        fanNotePageRepository.deleteByFanNoteId(fanNoteId)

        val newPages = command.pageImageUrls.mapIndexed { index, imageUrl ->
            FanNotePage.create(
                fanNoteId = savedFanNote.id.value,
                pageNumber = index + 1,
                imageUrl = imageUrl
            )
        }

        val savedPages = if (newPages.isNotEmpty()) {
            fanNotePageRepository.saveAll(newPages)
        } else {
            emptyList()
        }

        return FanNoteDetailResult.from(savedFanNote, savedPages)
    }
}
