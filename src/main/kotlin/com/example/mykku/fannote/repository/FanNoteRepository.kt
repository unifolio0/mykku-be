package com.example.mykku.fannote.repository

import com.example.mykku.fannote.domain.FanNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FanNoteRepository : JpaRepository<FanNote, Long> {

    @Query("SELECT fn FROM FanNote fn ORDER BY fn.productionDate DESC, fn.createdAt DESC")
    fun findAllOrderByProductionDateDesc(pageable: Pageable): Page<FanNote>

    @Query("SELECT fn FROM FanNote fn LEFT JOIN FETCH fn.pages WHERE fn.id = :id")
    fun findByIdWithPages(@Param("id") id: Long): FanNote?
}
