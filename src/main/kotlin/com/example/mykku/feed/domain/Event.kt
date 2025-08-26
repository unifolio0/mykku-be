package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "is_contest")
    var isContest: Boolean = false,

    @Column(name = "title")
    var title: String,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,
) : BaseEntity()
