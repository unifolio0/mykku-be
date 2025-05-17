package com.example.mykku.dailymessage.domain

import jakarta.persistence.*

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content")
    var content: String,

    @OneToMany(mappedBy = "dailyMessage", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val comments: MutableList<DailyMessageComment> = mutableListOf(),
) {
}
