package com.example.mykku.feed.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.exception.FeedException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "feed_tag")
class FeedTagJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: FeedJpaEntity,

    @Column(name = "title")
    var title: String
) : BaseEntity() {

    companion object {
        const val TITLE_MAX_LENGTH = 20
        val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")
    }

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw FeedException.tagTitleTooLong()
        }
        if (!VALID_PATTERN.matches(title)) {
            throw FeedException.tagInvalidFormat()
        }
    }

    fun toDomain(): FeedTag = FeedTag.reconstitute(
        id = id!!,
        title = title,
        feedId = feed.id!!,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
