package com.example.mykku.dailymessage.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.dailymessage.exception.DailyMessageException
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "content")
    var content: String,

    @Column(name = "date")
    var date: LocalDate,
) : BaseEntity() {
    companion object {
        const val CONTENT_MAX_LENGTH = 42
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw DailyMessageException.dailyMessageContentTooLong()
        }
    }
}
