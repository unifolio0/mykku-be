package com.example.mykku.member.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Member(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "nickname")
    val nickname: String,

    @Column(name = "follower_count")
    val followerCount: Int = 0,

    @Column(name = "following_count")
    val followingCount: Int = 0,

    @OneToMany(mappedBy = "member")
    val saveFeeds: List<SaveFeed> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val saveDailyMessages: List<SaveDailyMessage> = mutableListOf(),
) {
}
