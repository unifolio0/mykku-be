package com.example.mykku.board.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*
import java.util.*

@Entity
class Board(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    var title: String,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feeds: MutableList<Feed> = mutableListOf(),
) : BaseEntity() {
}
