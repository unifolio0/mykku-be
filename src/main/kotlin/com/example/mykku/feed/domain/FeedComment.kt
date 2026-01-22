package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class FeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    val parentComment: FeedComment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,
) : BaseEntity() {
    companion object {
        const val CONTENT_MAX_LENGTH = 1000
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedCommentContentTooLong()
        }
    }

    fun updateContent(newContent: String) {
        if (newContent.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedCommentContentTooLong()
        }
        this.content = newContent
    }
}
