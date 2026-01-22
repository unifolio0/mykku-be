package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.PagedContestsResult

interface GetMyParticipatedContestsUseCase {
    fun execute(memberId: String, page: Int, size: Int): PagedContestsResult
}
