package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.GetMyAwardsPreviewQuery
import com.example.mykku.contest.application.dto.MyAwardPreviewResult

interface GetMyAwardsPreviewUseCase {
    fun execute(query: GetMyAwardsPreviewQuery): List<MyAwardPreviewResult>
}
