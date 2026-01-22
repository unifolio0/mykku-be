package com.example.mykku.like.application.port.output

import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.domain.entity.LikeBoardEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LikeBoardPort {
    fun save(likeBoard: LikeBoardEntity): LikeBoardEntity
    fun existsByMemberIdAndBoardId(memberId: String, boardId: Long): Boolean
    fun deleteByMemberIdAndBoardId(memberId: String, boardId: Long)
    fun findAllByMemberId(memberId: String): List<LikeBoardEntity>
    fun findAllByMemberIdWithBoardInfo(memberId: String, pageable: Pageable): Page<LikeBoardInfoResult>
}
