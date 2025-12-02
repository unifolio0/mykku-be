package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestStatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {
    fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Contest>

    fun findByStatusAndExpiredAtAfter(status: ContestStatusType, dateTime: LocalDateTime): List<Contest>

    fun findByExpiredAtAfterOrderByCreatedAtDesc(dateTime: LocalDateTime, pageable: Pageable): Page<Contest>

    fun findByExpiredAtAfterOrderByCreatedAtAsc(dateTime: LocalDateTime, pageable: Pageable): Page<Contest>

    @Query(
        """
            SELECT c
            FROM Contest c
            WHERE c.expiredAt > :dateTime
            ORDER BY c.scrapCount DESC, c.createdAt DESC
        """
    )
    fun findActiveContestsByPopular(dateTime: LocalDateTime, pageable: Pageable): Page<Contest>

    fun findByExpiredAtLessThanEqualOrderByCreatedAtDesc(dateTime: LocalDateTime, pageable: Pageable): Page<Contest>

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Contest>
}
