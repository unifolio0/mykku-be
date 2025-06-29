package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyMessageRepository : JpaRepository<DailyMessage, Long> {
    fun findByDate(date: LocalDate): DailyMessage?
    
    @Query("SELECT dm FROM DailyMessage dm WHERE dm.date <= :date ORDER BY dm.date DESC")
    fun findByDateBeforeOrEqualOrderByDateDesc(@Param("date") date: LocalDate, pageable: Pageable): List<DailyMessage>
    
    @Query("SELECT dm FROM DailyMessage dm WHERE dm.date <= :date ORDER BY dm.date ASC")
    fun findByDateBeforeOrEqualOrderByDateAsc(@Param("date") date: LocalDate, pageable: Pageable): List<DailyMessage>
}
