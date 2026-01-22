package com.example.mykku.preference.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType
import com.example.mykku.preference.domain.vo.PreferenceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member_genre_preference")
class MemberGenrePreferenceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "genre_type", nullable = false, length = 50)
    val genreType: GenreType
) : BaseEntity() {

    fun toDomain(): MemberGenrePreference {
        return MemberGenrePreference.reconstitute(
            id = PreferenceId.of(id),
            memberId = member.id,
            genreType = genreType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: MemberGenrePreference, member: Member): MemberGenrePreferenceJpaEntity {
            return MemberGenrePreferenceJpaEntity(
                id = domain.id.value,
                member = member,
                genreType = domain.genreType
            )
        }
    }
}
