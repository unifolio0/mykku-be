package com.example.mykku.scrap.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveDailyMessage

interface SaveDailyMessageRepositoryPort {
    fun saveDailyMessage(member: Member, dailyMessage: DailyMessage): SaveDailyMessage
    fun unsaveDailyMessage(member: Member, dailyMessage: DailyMessage)
}
