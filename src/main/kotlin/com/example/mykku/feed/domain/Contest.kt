package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Contest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_winner_id")
    val contestWinners: MutableList<ContestWinner> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tags: MutableList<Tag> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feeds: MutableList<Feed> = mutableListOf(),
) {
}
