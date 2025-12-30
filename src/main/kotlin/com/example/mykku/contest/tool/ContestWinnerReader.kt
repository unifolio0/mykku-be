package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestWinnerRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ContestWinnerReader(
    private val contestWinnerRepository: ContestWinnerRepository
) {
    fun getWinnerById(winnerId: Long): ContestWinner {
        return contestWinnerRepository.findByIdOrNull(winnerId)
            ?: throw ContestException.contestWinnerNotFound()
    }

    fun getWinnersByContest(contest: Contest): List<ContestWinner> {
        return contestWinnerRepository.findByContest(contest)
    }

    fun getWinnersByContests(contests: List<Contest>): Map<Long, List<ContestWinner>> {
        val winners = contestWinnerRepository.findByContestIn(contests)
        return winners.groupBy { it.contest.id!! }
    }

    fun getWinnerByIdAndMember(winnerId: Long, member: Member): ContestWinner {
        val winner = getWinnerById(winnerId)
        if (winner.participation.member.id != member.id) {
            throw ContestException.notWinnerOwner()
        }
        return winner
    }

    fun existsByContest(contest: Contest): Boolean {
        return contestWinnerRepository.existsByContest(contest)
    }
}
