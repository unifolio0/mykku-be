package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.SetEventWinnersCommand
import com.example.mykku.event.application.dto.SetEventWinnersResult

interface SetEventWinnersUseCase {
    fun execute(command: SetEventWinnersCommand): SetEventWinnersResult
}
