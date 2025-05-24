package com.example.mykku.board.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feeds: MutableList<Feed> = mutableListOf(),
) : BaseEntity() {
}
