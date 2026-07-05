package com.example.mykku.contest.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.contest.domain.entity.ContestWinnerAnnouncement
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestWinnerAnnouncementId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "contest_winner_announcement")
class ContestWinnerAnnouncementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: ContestJpaEntity,

    @Column(name = "title")
    var title: String,

    @Column(name = "content", columnDefinition = "TEXT")
    var content: String,

    @Column(name = "announced_at")
    var announcedAt: LocalDate
) : BaseJpaEntity() {

    fun toDomain(): ContestWinnerAnnouncement {
        return ContestWinnerAnnouncement.reconstitute(
            id = ContestWinnerAnnouncementId.of(id!!),
            contestId = ContestId.of(contest.id!!),
            title = title,
            content = content,
            announcedAt = announcedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(announcement: ContestWinnerAnnouncement) {
        this.title = announcement.title
        this.content = announcement.content
        this.announcedAt = announcement.announcedAt
    }

    companion object {
        fun fromDomain(
            announcement: ContestWinnerAnnouncement,
            contestJpaEntity: ContestJpaEntity
        ): ContestWinnerAnnouncementJpaEntity {
            return ContestWinnerAnnouncementJpaEntity(
                id = if (announcement.id.value == 0L) null else announcement.id.value,
                contest = contestJpaEntity,
                title = announcement.title,
                content = announcement.content,
                announcedAt = announcement.announcedAt
            )
        }
    }
}
