package com.example.mykku.dailymessage.domain

import jakarta.persistence.*

@Entity
class DailyMessageComment(
    @Column(name = "content")
    val content: String,

    @Column(name = "like_count")
    val likeCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "daily_message_id")
    val dailyMessage: DailyMessage,

    @OneToOne
    @JoinColumn(name = "parent_comment_id")
    val parentComment: DailyMessageComment? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
