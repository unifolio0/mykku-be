package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("CONTEST")
class Contest(
    title: String,
    expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val contestTags: MutableList<ContestTag> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val contestWinners: MutableList<ContestWinner> = mutableListOf(),
) : BasicEvent(title = title, expiredAt = expiredAt) {
    override fun getType(): String = "CONTEST"
}
