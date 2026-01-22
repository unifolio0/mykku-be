package com.example.mykku.fannote.adapter.output.persistence

import com.example.mykku.fannote.adapter.output.persistence.entity.FanNotePageJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNoteJpaRepository
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNotePageJpaRepository
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.domain.entity.FanNotePage
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.domain.vo.FanNotePageId
import com.example.mykku.fannote.exception.FanNoteException
import org.springframework.stereotype.Component

@Component
class FanNotePageRepositoryAdapter(
    private val fanNotePageJpaRepository: FanNotePageJpaRepository,
    private val fanNoteJpaRepository: FanNoteJpaRepository
) : FanNotePageRepository {

    override fun save(page: FanNotePage): FanNotePage {
        val fanNoteJpaEntity = fanNoteJpaRepository.findById(page.fanNoteId)
            .orElseThrow { FanNoteException.fanNoteNotFound() }

        val jpaEntity = if (page.id.value == 0L) {
            FanNotePageJpaEntity.fromDomain(page, fanNoteJpaEntity)
        } else {
            fanNotePageJpaRepository.findById(page.id.value)
                .map { entity ->
                    entity.updateFromDomain(page)
                    entity
                }
                .orElseGet { FanNotePageJpaEntity.fromDomain(page, fanNoteJpaEntity) }
        }
        return fanNotePageJpaRepository.save(jpaEntity).toDomain()
    }

    override fun saveAll(pages: List<FanNotePage>): List<FanNotePage> {
        if (pages.isEmpty()) return emptyList()

        val fanNoteId = pages.first().fanNoteId
        val fanNoteJpaEntity = fanNoteJpaRepository.findById(fanNoteId)
            .orElseThrow { FanNoteException.fanNoteNotFound() }

        val jpaEntities = pages.map { page ->
            FanNotePageJpaEntity.fromDomain(page, fanNoteJpaEntity)
        }

        return fanNotePageJpaRepository.saveAll(jpaEntities)
            .map { it.toDomain() }
    }

    override fun findById(id: FanNotePageId): FanNotePage? {
        return fanNotePageJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByFanNoteIdOrderByPageNumber(fanNoteId: FanNoteId): List<FanNotePage> {
        return fanNotePageJpaRepository.findByFanNoteIdOrderByPageNumber(fanNoteId.value)
            .map { it.toDomain() }
    }

    override fun deleteByFanNoteId(fanNoteId: FanNoteId) {
        fanNotePageJpaRepository.deleteByFanNoteId(fanNoteId.value)
    }

    override fun deleteAll(pages: List<FanNotePage>) {
        val ids = pages.map { it.id.value }
        fanNotePageJpaRepository.deleteAllById(ids)
    }
}
