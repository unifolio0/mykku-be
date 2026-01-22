package com.example.mykku.scrap.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "save_daily_message")
class SaveDailyMessageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessageJpaEntity
) : BaseEntity() {

    fun toDomain(): SaveDailyMessageEntity {
        return SaveDailyMessageEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            dailyMessageId = this.dailyMessage.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: SaveDailyMessageEntity,
            member: MemberJpaEntity,
            dailyMessage: DailyMessageJpaEntity
        ): SaveDailyMessageJpaEntity {
            return SaveDailyMessageJpaEntity(
                id = domain.id?.value,
                member = member,
                dailyMessage = dailyMessage
            )
        }
    }
}
