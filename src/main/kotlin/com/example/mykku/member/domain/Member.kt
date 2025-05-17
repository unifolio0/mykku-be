package com.example.mykku.member.domain

import jakarta.persistence.*

@Entity
class Member(
    @Id
    var id: String,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "follower_count")
    var followerCount: Int = 0,

    @Column(name = "following_count")
    var followingCount: Int = 0,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val saveFeeds: List<SaveFeed> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val saveDailyMessages: List<SaveDailyMessage> = mutableListOf(),
) {
}
