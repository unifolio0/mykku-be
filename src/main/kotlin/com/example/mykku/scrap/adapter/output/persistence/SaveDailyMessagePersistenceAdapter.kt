package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.member.exception.MemberException
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
            ?: throw MemberException.memberNotFound()
        val dailyMessage = dailyMessageJpaRepository.findByIdOrNull(saveDailyMessage.dailyMessageId)
            ?: throw DailyMessageException.dailyMessageNotFound()

        val jpaEntity = SaveDailyMessageJpaEntity.fromDomain(saveDailyMessage, member, dailyMessage)
        return saveDailyMessageJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long): Boolean {
        return saveDailyMessageJpaRepository.existsByMemberIdAndDailyMessageId(memberId, dailyMessageId)
    }

    override fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveDailyMessageResult> {
        return saveDailyMessageJpaRepository.findByMemberId(memberId, pageable)
            .map { jpaEntity ->
                SaveDailyMessageResult(
                    id = jpaEntity.id!!,
                    dailyMessageId = jpaEntity.dailyMessage.id!!
                )
            }
    }

    override fun deleteByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long) {
        saveDailyMessageJpaRepository.deleteByMemberIdAndDailyMessageId(memberId, dailyMessageId)
    }
}
