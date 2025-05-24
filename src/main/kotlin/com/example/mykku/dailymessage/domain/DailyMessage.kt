package com.example.mykku.dailymessage.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "date", nullable = false)
    var date: LocalDate,

    @OneToMany(mappedBy = "dailyMessage", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_comment_id")
    val comments: MutableList<DailyMessageComment> = mutableListOf(),
) : BaseEntity() {
}
