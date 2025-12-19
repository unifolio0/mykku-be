package com.example.mykku.contest.infrastructure.adapter

import com.example.mykku.contest.application.port.out.ContestQueryPort
import com.example.mykku.contest.application.port.out.ContestSummary
import com.example.mykku.contest.domain.model.ContestId
import com.example.mykku.contest.repository.ContestRepository
import org.springframework.stereotype.Component

@Component
class ContestQueryAdapter(
    private val contestRepository: ContestRepository
) : ContestQueryPort {

    override fun findSummaryById(id: ContestId): ContestSummary? {
        return contestRepository.findById(id.value)
            .map { contest ->
                ContestSummary(
                    id = contest.id!!,
                    title = contest.title,
                    startedAt = contest.startedAt,
                    expiredAt = contest.expiredAt
                )
            }
            .orElse(null)
    }

    override fun existsById(id: ContestId): Boolean {
        return contestRepository.existsById(id.value)
    }
}
