package com.example.mykku.contest.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class ContestWinner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "winner_rank")
    var winnerRank: Int,

    @Column(name = "description")
    var description: String,

    @Column(name = "acceptance_speech")
    var acceptanceSpeech: String,

    @Column(name = "image")
    var image: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: Contest,
) : BaseEntity()
