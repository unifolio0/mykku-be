package com.example.mykku.member.domain

import jakarta.persistence.*

@Entity
class Follow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    val follower: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    val following: Member
) {
}
