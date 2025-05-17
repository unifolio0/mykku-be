package com.example.mykku.board.domain

import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var feeds: MutableList<Feed> = mutableListOf(),
) {
}
