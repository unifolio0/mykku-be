package com.example.mykku.dailymessage.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "date")
    var date: LocalDate,

    @OneToMany(mappedBy = "dailyMessage", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val comments: MutableList<DailyMessageComment> = mutableListOf(),
) : BaseEntity() {
    companion object {
        const val CONTENT_MAX_LENGTH = 42
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw MykkuException(ErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG)
        }
    }
}
