package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.member.repository.MemberRepository
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
    private val memberRepository: MemberRepository,
    private val dailyMessageRepository: DailyMessageRepository
) : SaveDailyMessagePort {

    override fun save(saveDailyMessage: SaveDailyMessageEntity): SaveDailyMessageEntity {
        val member = memberRepository.findByIdOrNull(saveDailyMessage.memberId)
            ?: throw IllegalArgumentException("Member not found: ${saveDailyMessage.memberId}")
        val dailyMessage = dailyMessageRepository.findByIdOrNull(saveDailyMessage.dailyMessageId)
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
