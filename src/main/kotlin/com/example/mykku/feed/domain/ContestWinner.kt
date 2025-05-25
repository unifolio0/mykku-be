package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.util.*

@Entity
class ContestWinner(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "rank", nullable = false)
    var rank: Int,

    @Column(name = "description")
    var description: String,

    @Column(name = "acceptance_speech")
    var acceptanceSpeech: String,

    @Column(name = "image", nullable = false)
    var image: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: Contest,
) {
}
