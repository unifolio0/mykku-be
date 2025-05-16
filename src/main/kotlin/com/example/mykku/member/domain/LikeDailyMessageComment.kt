package com.example.mykku.member.domain

import com.example.mykku.dailymessage.domain.DailyMessageComment
import jakarta.persistence.*

@Entity
class LikeDailyMessageComment(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne
    @JoinColumn(name = "daily_message_comment_id")
    val dailyMessageComment: DailyMessageComment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
