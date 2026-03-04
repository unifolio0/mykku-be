package com.example.mykku.scrap.application.usecase

import com.example.mykku.scrap.application.dto.GetSavedFanNotesQuery
import com.example.mykku.scrap.application.dto.SaveFanNoteCommand
import com.example.mykku.scrap.application.dto.SaveFanNoteResult
import com.example.mykku.scrap.application.dto.UnsaveFanNoteCommand
import com.example.mykku.scrap.application.port.input.SaveFanNoteUseCase
import com.example.mykku.scrap.application.port.output.SaveFanNotePort
import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveFanNoteApplicationService(
    private val saveFanNotePort: SaveFanNotePort
) : SaveFanNoteUseCase {

    @Transactional
    override fun saveFanNote(command: SaveFanNoteCommand) {
        if (saveFanNotePort.existsByMemberIdAndFanNoteId(command.memberId, command.fanNoteId)) {
            throw ScrapException.saveFanNoteAlreadyExists()
        }

        val saveFanNote = SaveFanNoteEntity.create(
            memberId = command.memberId,
            fanNoteId = command.fanNoteId
        )

        saveFanNotePort.save(saveFanNote)
    }

    @Transactional
    override fun unsaveFanNote(command: UnsaveFanNoteCommand) {
        if (!saveFanNotePort.existsByMemberIdAndFanNoteId(command.memberId, command.fanNoteId)) {
            throw ScrapException.saveFanNoteNotFound()
        }
        saveFanNotePort.deleteByMemberIdAndFanNoteId(command.memberId, command.fanNoteId)
    }

    @Transactional(readOnly = true)
    override fun getSavedFanNotes(query: GetSavedFanNotesQuery): Page<SaveFanNoteResult> {
        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return saveFanNotePort.findByMemberId(query.memberId, pageable)
    }

    @Transactional(readOnly = true)
    override fun isSaved(memberId: Long, fanNoteId: Long): Boolean {
        return saveFanNotePort.existsByMemberIdAndFanNoteId(memberId, fanNoteId)
    }
}
