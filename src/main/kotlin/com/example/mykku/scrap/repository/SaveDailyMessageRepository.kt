package com.example.mykku.scrap.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveDailyMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveDailyMessageRepository : JpaRepository<SaveDailyMessage, Long> {
    fun existsByMemberAndDailyMessage(member: Member, dailyMessage: DailyMessage): Boolean
    fun findByMember(member: Member, pageable: Pageable): Page<SaveDailyMessage>
    fun findByMemberAndDailyMessage(member: Member, dailyMessage: DailyMessage): SaveDailyMessage?
    fun deleteByMemberAndDailyMessage(member: Member, dailyMessage: DailyMessage)
}
