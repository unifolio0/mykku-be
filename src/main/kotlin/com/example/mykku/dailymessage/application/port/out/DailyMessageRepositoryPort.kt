package com.example.mykku.dailymessage.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessage

interface DailyMessageRepositoryPort {
    fun save(dailyMessage: DailyMessage): DailyMessage
    fun deleteById(id: Long)
}
