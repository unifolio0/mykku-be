package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.application.dto.SaveFanNoteResult
import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveFanNotePort {
    fun save(saveFanNote: SaveFanNoteEntity): SaveFanNoteEntity
    fun existsByMemberIdAndFanNoteId(memberId: String, fanNoteId: Long): Boolean
    fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveFanNoteResult>
    fun deleteByMemberIdAndFanNoteId(memberId: String, fanNoteId: Long)
}
