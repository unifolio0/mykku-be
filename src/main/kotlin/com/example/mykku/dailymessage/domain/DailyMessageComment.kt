package com.example.mykku.dailymessage.domain

import jakarta.persistence.*

@Entity
class DailyMessageComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessage,

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    val parentComment: DailyMessageComment? = null,
) {
}
