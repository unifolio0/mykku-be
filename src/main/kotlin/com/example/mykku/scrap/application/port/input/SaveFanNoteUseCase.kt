package com.example.mykku.scrap.application.port.input

import com.example.mykku.scrap.application.dto.GetSavedFanNotesQuery
import com.example.mykku.scrap.application.dto.SaveFanNoteCommand
import com.example.mykku.scrap.application.dto.SaveFanNoteResult
import com.example.mykku.scrap.application.dto.UnsaveFanNoteCommand
import org.springframework.data.domain.Page

interface SaveFanNoteUseCase {
    fun saveFanNote(command: SaveFanNoteCommand)
    fun unsaveFanNote(command: UnsaveFanNoteCommand)
    fun getSavedFanNotes(query: GetSavedFanNotesQuery): Page<SaveFanNoteResult>
    fun isSaved(memberId: String, fanNoteId: Long): Boolean
}
