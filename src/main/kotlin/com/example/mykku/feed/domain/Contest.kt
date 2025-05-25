package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Contest(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val contestWinners: MutableList<ContestWinner> = mutableListOf(),
) {
}
