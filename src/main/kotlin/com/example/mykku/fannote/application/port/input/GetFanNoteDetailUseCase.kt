package com.example.mykku.fannote.application.port.input

import com.example.mykku.fannote.application.dto.FanNoteDetailResult

interface GetFanNoteDetailUseCase {
    fun execute(fanNoteId: Long): FanNoteDetailResult
}
