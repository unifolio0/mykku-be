package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class TagImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "url")
    var url: String,

    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
}
