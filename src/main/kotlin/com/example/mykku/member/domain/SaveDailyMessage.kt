package com.example.mykku.member.domain

import com.example.mykku.dailymessage.domain.DailyMessage
import jakarta.persistence.*

@Entity
class SaveDailyMessage(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessage,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
