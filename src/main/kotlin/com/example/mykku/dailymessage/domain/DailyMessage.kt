package com.example.mykku.dailymessage.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class DailyMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "date")
    var date: LocalDate,

    @OneToMany(mappedBy = "dailyMessage", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val comments: MutableList<DailyMessageComment> = mutableListOf(),
) : BaseEntity() {
}
