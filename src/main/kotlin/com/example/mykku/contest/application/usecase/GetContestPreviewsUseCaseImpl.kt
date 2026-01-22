package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestPreviewResult
import com.example.mykku.contest.application.port.input.GetContestPreviewsUseCase
import com.example.mykku.contest.application.port.output.ContestImageRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class GetContestPreviewsUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestImageRepository: ContestImageRepository
) : GetContestPreviewsUseCase {

    @Transactional(readOnly = true)
    override fun execute(): List<ContestPreviewResult> {
        val contests = contestRepository.findByExpiredAtAfter(LocalDateTime.now()).take(5)

        if (contests.isEmpty()) {
            return emptyList()
        }

        val contestIds = contests.map { it.id }
        val imagesByContestId = contestImageRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }

        return contests.map { contest ->
            val images = imagesByContestId[contest.id.value] ?: emptyList()
            ContestPreviewResult(
                id = contest.id.value,
                title = contest.title,
                thumbnailUrl = images.sortedBy { it.orderIndex }.firstOrNull()?.url
            )
        }
    }
}
