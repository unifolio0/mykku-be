package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.GetMyWinnerStatusQuery
import com.example.mykku.contest.application.dto.MyWinnerStatusResult

interface GetMyWinnerStatusUseCase {
    fun execute(query: GetMyWinnerStatusQuery): MyWinnerStatusResult
}
