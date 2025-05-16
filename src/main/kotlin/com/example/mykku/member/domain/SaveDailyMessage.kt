package com.example.mykku.member.domain

import com.example.mykku.feed.domain.FeedComment
import jakarta.persistence.*

@Entity
class SaveDailyMessage(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: FeedComment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
