package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import jakarta.persistence.*

@Entity
class FeedTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @Column(name = "title")
    var title: String,
) : BaseEntity() {
    companion object {
        const val TITLE_MAX_LENGTH = 20
        val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")
    }

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw MykkuException(ErrorCode.TAG_TITLE_TOO_LONG)
        }
        if (!VALID_PATTERN.matches(title)) {
            throw MykkuException(ErrorCode.TAG_INVALID_FORMAT)
        }
    }
}
