package com.example.mykku.scrap.repository

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveFanNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFanNoteRepository : JpaRepository<SaveFanNote, Long> {
    fun existsByMemberAndFanNote(member: Member, fanNote: FanNote): Boolean
    fun findByMember(member: Member, pageable: Pageable): Page<SaveFanNote>
    fun findByMemberAndFanNote(member: Member, fanNote: FanNote): SaveFanNote?
    fun deleteByMemberAndFanNote(member: Member, fanNote: FanNote)
}
