package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.util.*

@Entity
class TagImage(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "url", nullable = false)
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) : BaseEntity() {
}
