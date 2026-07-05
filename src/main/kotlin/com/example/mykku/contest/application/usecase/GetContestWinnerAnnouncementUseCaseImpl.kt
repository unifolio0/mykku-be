package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestWinnerAnnouncementResult
import com.example.mykku.contest.application.port.input.GetContestWinnerAnnouncementUseCase
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerAnnouncementRepository
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.exception.ContestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContestWinnerAnnouncementUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerAnnouncementRepository: ContestWinnerAnnouncementRepository
) : GetContestWinnerAnnouncementUseCase {

    @Transactional(readOnly = true)
    override fun execute(contestId: Long): ContestWinnerAnnouncementResult {
        val contest = contestRepository.findById(ContestId.of(contestId))
            ?: throw ContestException.contestNotFound()

        val announcement = contestWinnerAnnouncementRepository.findByContestId(contest.id)
            ?: throw ContestException.winnerAnnouncementNotFound()

        return ContestWinnerAnnouncementResult(
            contestId = contest.id.value,
            contestTitle = contest.title,
            title = announcement.title,
            content = announcement.content,
            announcedAt = announcement.announcedAt
        )
    }
}
