package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.UpdateAcceptanceSpeechCommand
import com.example.mykku.contest.application.dto.UpdateAcceptanceSpeechResult
import com.example.mykku.contest.application.port.input.UpdateAcceptanceSpeechUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.vo.ContestWinnerId
import com.example.mykku.contest.exception.ContestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAcceptanceSpeechUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository
) : UpdateAcceptanceSpeechUseCase {

    @Transactional
    override fun execute(command: UpdateAcceptanceSpeechCommand): UpdateAcceptanceSpeechResult {
        val winner = contestWinnerRepository.findById(ContestWinnerId.of(command.winnerId))
            ?: throw ContestException.contestWinnerNotFound()

        val participation = contestParticipationRepository.findById(winner.participationId)
            ?: throw ContestException.participationNotFound()

        if (participation.memberId != command.memberId) {
            throw ContestException.notWinnerOwner()
        }

        winner.updateAcceptanceSpeech(command.acceptanceSpeech)
        val updatedWinner = contestWinnerRepository.save(winner)

        return UpdateAcceptanceSpeechResult(
            winnerId = updatedWinner.id.value,
            acceptanceSpeech = updatedWinner.acceptanceSpeech
        )
    }
}
