package com.example.mykku.contest.adapter.output.persistence

import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.repository.ContestJpaRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.domain.entity.Contest
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestSortType
import com.example.mykku.contest.domain.vo.ContestStatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ContestRepositoryAdapter(
    private val contestJpaRepository: ContestJpaRepository
) : ContestRepository {

    override fun save(contest: Contest): Contest {
        val jpaEntity = ContestJpaEntity.fromDomain(contest)
        return contestJpaRepository.save(jpaEntity).toDomain()
    }

    override fun findById(id: ContestId): Contest? {
        return contestJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByIds(ids: List<ContestId>): List<Contest> {
        if (ids.isEmpty()) return emptyList()
        return contestJpaRepository.findAllById(ids.map { it.value })
            .map { it.toDomain() }
    }

    override fun findByStatus(status: ContestStatusType): List<Contest> {
        return contestJpaRepository.findByStatus(status)
            .map { it.toDomain() }
    }

    override fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Contest> {
        return contestJpaRepository.findByExpiredAtAfter(dateTime)
            .map { it.toDomain() }
    }

    override fun findByStatusAndExpiredAtAfter(status: ContestStatusType, dateTime: LocalDateTime): List<Contest> {
        return contestJpaRepository.findByStatusAndExpiredAtAfter(status, dateTime)
            .map { it.toDomain() }
    }

    override fun findWithPagination(
        status: ContestStatusType,
        sortType: ContestSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Contest> {
        val page = when (status) {
            ContestStatusType.ALL -> {
                contestJpaRepository.findAllByOrderByCreatedAtDesc(pageable)
            }
            ContestStatusType.ACTIVE, ContestStatusType.WINNER_SELECTING -> {
                when (sortType) {
                    ContestSortType.LATEST -> contestJpaRepository.findByExpiredAtAfterOrderByCreatedAtDesc(currentTime, pageable)
                    ContestSortType.OLDEST -> contestJpaRepository.findByExpiredAtAfterOrderByCreatedAtAsc(currentTime, pageable)
                    ContestSortType.POPULAR -> contestJpaRepository.findActiveContestsByPopular(currentTime, pageable)
                }
            }
            ContestStatusType.EXPIRED, ContestStatusType.WINNER_SELECTED -> {
                contestJpaRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)
            }
        }
        return page.map { it.toDomain() }
    }
}
