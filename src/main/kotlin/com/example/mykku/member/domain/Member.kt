package com.example.mykku.member.domain

import jakarta.persistence.*

@Entity
class Member(
    @Id
    var id: String,

    @Column(name = "nickname", nullable = false)
    var nickname: String,

    @Column(name = "follower_count", nullable = false)
    var followerCount: Int = 0,

    @Column(name = "following_count", nullable = false)
    var followingCount: Int = 0,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val saveFeeds: MutableList<SaveFeed> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val saveDailyMessages: MutableList<SaveDailyMessage> = mutableListOf(),
) {
}
