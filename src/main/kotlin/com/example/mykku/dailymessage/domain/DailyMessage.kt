package com.example.mykku.dailymessage.domain

import jakarta.persistence.*

@Entity
class DailyMessage(
    @Column(name = "content")
    val content: String,

    @OneToMany(mappedBy = "dailyMessage")
    val comments: List<DailyMessageComment> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
