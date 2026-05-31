package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.GetMyEventWinnerStatusQuery
import com.example.mykku.event.application.dto.MyEventWinnerStatusResult

interface GetMyEventWinnerStatusUseCase {
    fun execute(query: GetMyEventWinnerStatusQuery): MyEventWinnerStatusResult
}
