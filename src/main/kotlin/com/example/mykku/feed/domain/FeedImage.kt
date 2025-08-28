package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class FeedImage(
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
    val feed: Feed,
) : BaseEntity()
