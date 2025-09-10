package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "follow",
    indexes = [
        Index(name = "idx_follow_follower", columnList = "follower_id"),
        Index(name = "idx_follow_following", columnList = "following_id"),
        Index(name = "idx_follow_composite", columnList = "follower_id, following_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["follower_id", "following_id"])
    ]
)
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
) : BaseEntity() {
}
