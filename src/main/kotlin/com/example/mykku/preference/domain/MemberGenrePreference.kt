package com.example.mykku.preference.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
@Table(name = "member_genre_preference")
class MemberGenrePreference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "genre_type", nullable = false, length = 50)
    val genreType: GenreType
) : BaseEntity()
