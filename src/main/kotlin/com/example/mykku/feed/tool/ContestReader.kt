package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.ContestWinnersResponse
import com.example.mykku.feed.repository.ContestRepository
import org.springframework.stereotype.Service

@Service
class ContestReader(
    private val contestRepository: ContestRepository
) {
    fun getContestWinners(): List<ContestWinnersResponse> {
        return contestRepository.findAll()
            .map { contest -> ContestWinnersResponse(contest) }
            .take(2)
    }
}
