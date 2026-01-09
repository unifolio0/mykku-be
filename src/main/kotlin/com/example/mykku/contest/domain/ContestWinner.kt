package com.example.mykku.contest.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ContestWinner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "winner_rank")
    var winnerRank: Int,

    @Column(name = "description")
    var description: String = "",

    @Column(name = "acceptance_speech")
    var acceptanceSpeech: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: Contest,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    val participation: ContestParticipation
) : BaseEntity() {
    fun updateAcceptanceSpeech(speech: String) {
        this.acceptanceSpeech = speech
    }
}
