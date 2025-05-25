package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.dailymessage.domain.DailyMessageComment
import jakarta.persistence.*
import java.util.*

@Entity
class LikeDailyMessageComment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_comment_id")
    val dailyMessageComment: DailyMessageComment,
) : BaseEntity() {
}
