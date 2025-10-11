package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.SaveDailyMessage
import org.springframework.data.domain.Page

data class SaveDailyMessageResponse(
    val id: Long,
    val dailyMessageId: Long
) {
    companion object {
        fun from(saveDailyMessage: SaveDailyMessage): SaveDailyMessageResponse {
            return SaveDailyMessageResponse(
                id = saveDailyMessage.id!!,
                dailyMessageId = saveDailyMessage.dailyMessage.id!!
            )
        }

        fun fromPage(page: Page<SaveDailyMessage>): Page<SaveDailyMessageResponse> {
            return page.map { from(it) }
        }
    }
}
