package com.example.mykku.fannote.application.port.input

interface DeleteFanNoteUseCase {
    fun execute(fanNoteId: Long)
}
