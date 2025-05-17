package com.example.mykku.member.domain

import com.example.mykku.common.BaseEntity
import com.example.mykku.dailymessage.domain.DailyMessageComment
import jakarta.persistence.*

@Entity
class LikeDailyMessageComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_comment_id")
    val dailyMessageComment: DailyMessageComment,
) : BaseEntity() {
}
