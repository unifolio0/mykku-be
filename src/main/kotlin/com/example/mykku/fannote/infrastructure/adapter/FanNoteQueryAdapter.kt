package com.example.mykku.fannote.infrastructure.adapter

import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import com.example.mykku.fannote.application.port.out.FanNoteSummary
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.domain.model.FanNoteId
import com.example.mykku.fannote.exception.FanNoteException
import com.example.mykku.fannote.repository.FanNotePageRepository
import com.example.mykku.fannote.repository.FanNoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FanNoteQueryAdapter(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
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

    override fun findAllWithPagination(pageable: Pageable): Page<FanNote> {
        return fanNoteRepository.findAll(pageable)
    }

    override fun findById(id: Long): FanNote {
        return fanNoteRepository.findById(id).orElseThrow {
            FanNoteException.fanNoteNotFound()
        }
    }

    override fun findPagesByFanNoteId(fanNoteId: Long): List<FanNotePage> {
        return fanNotePageRepository.findByFanNoteIdOrderByPageNumber(fanNoteId)
    }
}
