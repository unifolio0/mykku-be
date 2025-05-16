package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class TagImage(
    @Column(name = "url")
    val url: String,

    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
