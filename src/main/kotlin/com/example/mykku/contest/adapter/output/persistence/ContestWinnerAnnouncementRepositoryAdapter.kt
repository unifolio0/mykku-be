package com.example.mykku.contest.adapter.output.persistence

import com.example.mykku.contest.adapter.output.persistence.entity.ContestWinnerAnnouncementJpaEntity
import com.example.mykku.contest.adapter.output.persistence.repository.ContestJpaRepository
import com.example.mykku.contest.adapter.output.persistence.repository.ContestWinnerAnnouncementJpaRepository
import com.example.mykku.contest.application.port.output.ContestWinnerAnnouncementRepository
import com.example.mykku.contest.domain.entity.ContestWinnerAnnouncement
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.exception.ContestException
import org.springframework.stereotype.Repository

@Repository
class ContestWinnerAnnouncementRepositoryAdapter(
    private val contestWinnerAnnouncementJpaRepository: ContestWinnerAnnouncementJpaRepository,
    private val contestJpaRepository: ContestJpaRepository
) : ContestWinnerAnnouncementRepository {

    override fun save(announcement: ContestWinnerAnnouncement): ContestWinnerAnnouncement {
        val contestJpaEntity = contestJpaRepository.findById(announcement.contestId.value)
            .orElseThrow { ContestException.contestNotFound() }

        val existing = contestWinnerAnnouncementJpaRepository.findByContest(contestJpaEntity)
        val saved = if (existing != null) {
            existing.updateFromDomain(announcement)
            contestWinnerAnnouncementJpaRepository.save(existing)
        } else {
            val jpaEntity = ContestWinnerAnnouncementJpaEntity.fromDomain(announcement, contestJpaEntity)
            contestWinnerAnnouncementJpaRepository.save(jpaEntity)
        }
        return saved.toDomain()
    }

    override fun findByContestId(contestId: ContestId): ContestWinnerAnnouncement? {
        val contestJpaEntity = contestJpaRepository.findById(contestId.value).orElse(null)
            ?: return null
        return contestWinnerAnnouncementJpaRepository.findByContest(contestJpaEntity)?.toDomain()
    }
}
