package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.scrap.adapter.output.persistence.entity.SaveDailyMessageJpaEntity
import com.example.mykku.scrap.application.dto.SaveDailyMessageResult
import com.example.mykku.scrap.application.port.output.SaveDailyMessagePort
import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SaveDailyMessagePersistenceAdapter(
    private val saveDailyMessageJpaRepository: SaveDailyMessageJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val dailyMessageJpaRepository: DailyMessageJpaRepository
) : SaveDailyMessagePort {

    override fun save(saveDailyMessage: SaveDailyMessageEntity): SaveDailyMessageEntity {
        val member = memberJpaRepository.findByIdOrNull(saveDailyMessage.memberId)
            ?: throw IllegalArgumentException("Member not found: ${saveDailyMessage.memberId}")
        val dailyMessage = dailyMessageJpaRepository.findByIdOrNull(saveDailyMessage.dailyMessageId)
            ?: throw IllegalArgumentException("DailyMessage not found: ${saveDailyMessage.dailyMessageId}")

        val jpaEntity = SaveDailyMessageJpaEntity.fromDomain(saveDailyMessage, member, dailyMessage)
        return saveDailyMessageJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndDailyMessageId(memberId: String, dailyMessageId: Long): Boolean {
        return saveDailyMessageJpaRepository.existsByMemberIdAndDailyMessageId(memberId, dailyMessageId)
    }

    override fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveDailyMessageResult> {
        return saveDailyMessageJpaRepository.findByMemberId(memberId, pageable)
            .map { jpaEntity ->
                SaveDailyMessageResult(
                    id = jpaEntity.id!!,
                    dailyMessageId = jpaEntity.dailyMessage.id!!
                )
            }
    }

    override fun deleteByMemberIdAndDailyMessageId(memberId: String, dailyMessageId: Long) {
        saveDailyMessageJpaRepository.deleteByMemberIdAndDailyMessageId(memberId, dailyMessageId)
    }
}
