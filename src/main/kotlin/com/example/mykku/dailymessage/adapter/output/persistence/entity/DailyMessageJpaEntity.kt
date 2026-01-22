package com.example.mykku.dailymessage.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "daily_message")
class DailyMessageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "content")
    var content: String,

    @Column(name = "date")
    var date: LocalDate
) : BaseEntity() {

    fun toDomain(): DailyMessage {
        return DailyMessage.reconstitute(
            id = DailyMessageId.of(id!!),
            title = title,
            content = content,
            date = date,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(dailyMessage: DailyMessage) {
        this.title = dailyMessage.title
        this.content = dailyMessage.content
        this.date = dailyMessage.date
    }

    companion object {
        fun fromDomain(dailyMessage: DailyMessage): DailyMessageJpaEntity {
            return DailyMessageJpaEntity(
                id = if (dailyMessage.id.value == 0L) null else dailyMessage.id.value,
                title = dailyMessage.title,
                content = dailyMessage.content,
                date = dailyMessage.date
            )
        }
    }
}
