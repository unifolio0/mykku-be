package com.example.mykku.fannote.application.port.out

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.domain.model.FanNoteId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    // Cross-domain methods (for internal use)
    fun findAllWithPagination(pageable: Pageable): Page<FanNote>
    fun findById(id: Long): FanNote
    fun findPagesByFanNoteId(fanNoteId: Long): List<FanNotePage>
}
