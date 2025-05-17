package com.example.mykku.dailymessage.domain

import com.example.mykku.common.BaseEntity
import jakarta.persistence.*

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content", nullable = false)
    var content: String,

    @OneToMany(mappedBy = "dailyMessage", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var comments: MutableList<DailyMessageComment> = mutableListOf(),
) : BaseEntity() {
}
