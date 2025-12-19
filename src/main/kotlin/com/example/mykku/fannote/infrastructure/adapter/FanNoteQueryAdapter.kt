package com.example.mykku.fannote.infrastructure.adapter

import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import com.example.mykku.fannote.application.port.out.FanNoteSummary
import com.example.mykku.fannote.domain.model.FanNoteId
import com.example.mykku.fannote.repository.FanNoteRepository
import org.springframework.stereotype.Component

@Component
class FanNoteQueryAdapter(
    private val fanNoteRepository: FanNoteRepository
) : FanNoteQueryPort {

    override fun findSummaryById(id: FanNoteId): FanNoteSummary? {
        return fanNoteRepository.findById(id.value)
            .map { fn ->
                FanNoteSummary(
                    id = fn.id!!,
                    title = fn.title,
                    subtitle = fn.subtitle,
                    productionDate = fn.productionDate,
                    coverImageUrl = fn.coverImageUrl
                )
            }
            .orElse(null)
    }

    override fun existsById(id: FanNoteId): Boolean {
        return fanNoteRepository.existsById(id.value)
    }
}
