package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestWinnerAnnouncementResult
import com.example.mykku.contest.application.dto.UpsertContestWinnerAnnouncementCommand
import com.example.mykku.contest.application.port.input.UpsertContestWinnerAnnouncementUseCase
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerAnnouncementRepository
import com.example.mykku.contest.domain.entity.ContestWinnerAnnouncement
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.exception.ContestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpsertContestWinnerAnnouncementUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerAnnouncementRepository: ContestWinnerAnnouncementRepository
) : UpsertContestWinnerAnnouncementUseCase {

    @Transactional
    override fun execute(command: UpsertContestWinnerAnnouncementCommand): ContestWinnerAnnouncementResult {
        val contest = contestRepository.findById(ContestId.of(command.contestId))
            ?: throw ContestException.contestNotFound()

        val existing = contestWinnerAnnouncementRepository.findByContestId(contest.id)
        val announcement = existing?.update(command.title, command.content, command.announcedAt)
            ?: ContestWinnerAnnouncement.create(contest.id, command.title, command.content, command.announcedAt)

        val saved = contestWinnerAnnouncementRepository.save(announcement)

        return ContestWinnerAnnouncementResult(
            contestId = contest.id.value,
            contestTitle = contest.title,
            title = saved.title,
            content = saved.content,
            announcedAt = saved.announcedAt
        )
    }
}
