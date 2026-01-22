package com.example.mykku.fannote.application.port.input

import com.example.mykku.fannote.application.dto.CreateFanNoteCommand
import com.example.mykku.fannote.application.dto.FanNoteDetailResult

interface CreateFanNoteUseCase {
    fun execute(command: CreateFanNoteCommand): FanNoteDetailResult
}
