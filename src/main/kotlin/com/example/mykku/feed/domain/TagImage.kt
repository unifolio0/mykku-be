package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class TagImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "url", nullable = false)
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) : BaseEntity() {
}
