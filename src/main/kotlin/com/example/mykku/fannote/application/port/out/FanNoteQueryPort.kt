package com.example.mykku.fannote.application.port.out

import com.example.mykku.fannote.domain.model.FanNoteId
import java.time.LocalDate

data class FanNoteSummary(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val productionDate: LocalDate,
    val coverImageUrl: String?
)

interface FanNoteQueryPort {
    fun findSummaryById(id: FanNoteId): FanNoteSummary?
    fun existsById(id: FanNoteId): Boolean
}
