package com.example.mykku.scrap.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveDailyMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveDailyMessageQueryPort {
    fun isSaved(member: Member, dailyMessage: DailyMessage): Boolean
    fun getSavedDailyMessages(member: Member, pageable: Pageable): Page<SaveDailyMessage>
}
