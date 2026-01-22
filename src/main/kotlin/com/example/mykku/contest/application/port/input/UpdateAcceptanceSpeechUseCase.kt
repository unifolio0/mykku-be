package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.UpdateAcceptanceSpeechCommand
import com.example.mykku.contest.application.dto.UpdateAcceptanceSpeechResult

interface UpdateAcceptanceSpeechUseCase {
    fun execute(command: UpdateAcceptanceSpeechCommand): UpdateAcceptanceSpeechResult
}
