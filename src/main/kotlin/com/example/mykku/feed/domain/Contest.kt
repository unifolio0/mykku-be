package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Contest(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title")
    var title: String,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val contestTags: MutableList<ContestTag> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val contestWinners: MutableList<ContestWinner> = mutableListOf(),
) : BaseEntity() {
}
