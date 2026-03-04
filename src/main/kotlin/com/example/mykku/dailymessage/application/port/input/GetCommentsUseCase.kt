package com.example.mykku.dailymessage.application.port.input

import com.example.mykku.dailymessage.application.dto.DailyMessageCommentsResult
import org.springframework.data.domain.Pageable

interface GetCommentsUseCase {
    fun execute(dailyMessageId: Long, memberId: Long?, pageable: Pageable): DailyMessageCommentsResult
}
