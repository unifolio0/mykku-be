package com.example.mykku.feed.service

import com.example.mykku.feed.dto.ContestWinnersResponse
import com.example.mykku.feed.repository.ContestRepository
import org.springframework.stereotype.Service

@Service
class ContestService(
    private val contestRepository: ContestRepository
) {
    fun getContestWinners(): List<ContestWinnersResponse> {
        return contestRepository.findAll()
            .map { contest -> ContestWinnersResponse(contest) }
            .take(2)
    }
}
