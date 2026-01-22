package com.example.mykku.fannote.adapter.output.persistence

import com.example.mykku.fannote.adapter.output.persistence.entity.FanNoteJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNoteJpaRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.vo.FanNoteId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FanNoteRepositoryAdapter(
    private val fanNoteJpaRepository: FanNoteJpaRepository
) : FanNoteRepository {

    override fun save(fanNote: FanNote): FanNote {
        val jpaEntity = if (fanNote.id.value == 0L) {
            FanNoteJpaEntity.fromDomain(fanNote)
        } else {
            fanNoteJpaRepository.findById(fanNote.id.value)
                .map { entity ->
                    entity.updateFromDomain(fanNote)
                    entity
                }
                .orElseGet { FanNoteJpaEntity.fromDomain(fanNote) }
        }
        return fanNoteJpaRepository.save(jpaEntity).toDomain()
    }

    override fun findById(id: FanNoteId): FanNote? {
        return fanNoteJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAll(pageable: Pageable): Page<FanNote> {
        return fanNoteJpaRepository.findAll(pageable)
            .map { it.toDomain() }
    }

    override fun existsById(id: FanNoteId): Boolean {
        return fanNoteJpaRepository.existsById(id.value)
    }

    override fun deleteById(id: FanNoteId) {
        fanNoteJpaRepository.deleteById(id.value)
    }
}
