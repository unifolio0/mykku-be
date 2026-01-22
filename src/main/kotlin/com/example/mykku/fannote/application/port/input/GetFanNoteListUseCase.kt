package com.example.mykku.fannote.application.port.input

import com.example.mykku.fannote.application.dto.FanNoteListResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetFanNoteListUseCase {
    fun execute(pageable: Pageable): Page<FanNoteListResult>
}
