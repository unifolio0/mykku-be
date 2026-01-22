package com.example.mykku.feed.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.entity.FeedImage
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
@Table(name = "feed_image")
class FeedImageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "url")
    var url: String,

    @Column(name = "width")
    var width: Int,

    @Column(name = "height")
    var height: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: FeedJpaEntity
) : BaseEntity() {

    fun toDomain(): FeedImage = FeedImage.reconstitute(
        id = id!!,
        url = url,
        width = width,
        height = height,
        feedId = feed.id!!,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
