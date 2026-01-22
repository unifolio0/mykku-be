package com.example.mykku.scrap.application.port.input

import com.example.mykku.scrap.application.dto.GetSavedDailyMessagesQuery
import com.example.mykku.scrap.application.dto.SaveDailyMessageCommand
import com.example.mykku.scrap.application.dto.SaveDailyMessageResult
import com.example.mykku.scrap.application.dto.UnsaveDailyMessageCommand
import org.springframework.data.domain.Page

interface SaveDailyMessageUseCase {
    fun saveDailyMessage(command: SaveDailyMessageCommand)
    fun unsaveDailyMessage(command: UnsaveDailyMessageCommand)
    fun getSavedDailyMessages(query: GetSavedDailyMessagesQuery): Page<SaveDailyMessageResult>
    fun isSaved(memberId: String, dailyMessageId: Long): Boolean
}
