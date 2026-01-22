package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.event.repository.EventRepository
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.adapter.output.persistence.entity.SaveEventJpaEntity
import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.application.port.output.SaveEventPort
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SaveEventPersistenceAdapter(
    private val saveEventJpaRepository: SaveEventJpaRepository,
    private val memberRepository: MemberRepository,
    private val eventRepository: EventRepository
) : SaveEventPort {

    override fun save(saveEvent: SaveEventEntity): SaveEventEntity {
        val member = memberRepository.findByIdOrNull(saveEvent.memberId)
            ?: throw IllegalArgumentException("Member not found: ${saveEvent.memberId}")
        val event = eventRepository.findByIdOrNull(saveEvent.eventId)
            ?: throw IllegalArgumentException("Event not found: ${saveEvent.eventId}")

        val jpaEntity = SaveEventJpaEntity.fromDomain(saveEvent, member, event)
        return saveEventJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndEventId(memberId: String, eventId: Long): Boolean {
        return saveEventJpaRepository.existsByMemberIdAndEventId(memberId, eventId)
    }

    override fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveEventResult> {
        return saveEventJpaRepository.findByMemberId(memberId, pageable)
            .map { jpaEntity ->
                SaveEventResult(
                    id = jpaEntity.id!!,
                    eventId = jpaEntity.event.id!!
                )
            }
    }

    override fun deleteByMemberIdAndEventId(memberId: String, eventId: Long) {
        saveEventJpaRepository.deleteByMemberIdAndEventId(memberId, eventId)
    }

    override fun findByMemberIdAndEventIdIn(memberId: String, eventIds: List<Long>): List<SaveEventEntity> {
        return saveEventJpaRepository.findByMemberIdAndEventIdIn(memberId, eventIds)
            .map { it.toDomain() }
    }
}
