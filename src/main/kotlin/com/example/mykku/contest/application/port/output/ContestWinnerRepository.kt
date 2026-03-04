package com.example.mykku.contest.application.port.output

import com.example.mykku.contest.domain.entity.ContestWinner
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import com.example.mykku.contest.domain.vo.ContestWinnerId

interface ContestWinnerRepository {
    fun save(winner: ContestWinner): ContestWinner
    fun saveAll(winners: List<ContestWinner>): List<ContestWinner>
    fun findById(id: ContestWinnerId): ContestWinner?
    fun findByContestId(contestId: ContestId): List<ContestWinner>
    fun findByContestIdAndMemberId(contestId: ContestId, memberId: Long): ContestWinner?
    fun findByContestIds(contestIds: List<ContestId>): List<ContestWinner>
    fun existsByContestId(contestId: ContestId): Boolean
    fun deleteAllByContestId(contestId: ContestId)
    fun deleteAllByParticipationIds(participationIds: List<ContestParticipationId>)
}
