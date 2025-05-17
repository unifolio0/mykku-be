package com.example.mykku.dailymessage.domain

import com.example.mykku.common.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class DailyMessageComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessage,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    val parentComment: DailyMessageComment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) : BaseEntity() {
}
