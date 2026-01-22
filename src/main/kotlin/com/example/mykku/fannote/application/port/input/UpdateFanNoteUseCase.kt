package com.example.mykku.fannote.application.port.input

import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.dto.UpdateFanNoteCommand

interface UpdateFanNoteUseCase {
    fun execute(command: UpdateFanNoteCommand): FanNoteDetailResult
}
