package com.example.mykku.dailymessage.domain

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

    @ManyToOne
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessage,

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    val parentComment: DailyMessageComment? = null,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,
) {
}
