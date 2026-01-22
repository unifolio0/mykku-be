package com.example.mykku.contest.application.port.output

import com.example.mykku.contest.domain.entity.Contest
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestSortType
import com.example.mykku.contest.domain.vo.ContestStatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface ContestRepository {
    fun save(contest: Contest): Contest
    fun findById(id: ContestId): Contest?
    fun findByStatus(status: ContestStatusType): List<Contest>
    fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Contest>
    fun findByStatusAndExpiredAtAfter(status: ContestStatusType, dateTime: LocalDateTime): List<Contest>
    fun findWithPagination(
        status: ContestStatusType,
        sortType: ContestSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Contest>
}
