package com.example.mykku.board.domain

import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class Board(
    @Column(name = "title")
    val title: String,

    @OneToMany(mappedBy = "board")
    val feeds: List<Feed> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
