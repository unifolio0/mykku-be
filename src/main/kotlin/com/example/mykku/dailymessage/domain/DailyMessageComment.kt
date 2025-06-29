package com.example.mykku.dailymessage.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class DailyMessageComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
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
    companion object {
        const val CONTENT_MAX_LENGTH = 1000
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw MykkuException(ErrorCode.DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG)
        }
    }
    
    fun updateContent(newContent: String) {
        if (newContent.length > CONTENT_MAX_LENGTH) {
            throw MykkuException(ErrorCode.DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG)
        }
        this.content = newContent
    }
}
