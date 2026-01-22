package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.fannote.repository.FanNoteRepository
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFanNoteJpaEntity
import com.example.mykku.scrap.application.dto.SaveFanNoteResult
import com.example.mykku.scrap.application.port.output.SaveFanNotePort
import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SaveFanNotePersistenceAdapter(
    private val saveFanNoteJpaRepository: SaveFanNoteJpaRepository,
    private val memberRepository: MemberRepository,
    private val fanNoteRepository: FanNoteRepository
) : SaveFanNotePort {

    override fun save(saveFanNote: SaveFanNoteEntity): SaveFanNoteEntity {
        val member = memberRepository.findByIdOrNull(saveFanNote.memberId)
            ?: throw IllegalArgumentException("Member not found: ${saveFanNote.memberId}")
        val fanNote = fanNoteRepository.findByIdOrNull(saveFanNote.fanNoteId)
            ?: throw IllegalArgumentException("FanNote not found: ${saveFanNote.fanNoteId}")

        val jpaEntity = SaveFanNoteJpaEntity.fromDomain(saveFanNote, member, fanNote)
        return saveFanNoteJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFanNoteId(memberId: String, fanNoteId: Long): Boolean {
        return saveFanNoteJpaRepository.existsByMemberIdAndFanNoteId(memberId, fanNoteId)
    }

    override fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveFanNoteResult> {
        return saveFanNoteJpaRepository.findByMemberId(memberId, pageable)
            .map { jpaEntity ->
                SaveFanNoteResult(
                    id = jpaEntity.id!!,
                    fanNoteId = jpaEntity.fanNote.id!!
                )
            }
    }

    override fun deleteByMemberIdAndFanNoteId(memberId: String, fanNoteId: Long) {
        saveFanNoteJpaRepository.deleteByMemberIdAndFanNoteId(memberId, fanNoteId)
    }
}
