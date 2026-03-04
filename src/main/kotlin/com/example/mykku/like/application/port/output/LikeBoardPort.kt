package com.example.mykku.like.application.port.output

import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.domain.entity.LikeBoardEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LikeBoardPort {
    fun save(likeBoard: LikeBoardEntity): LikeBoardEntity
    fun existsByMemberIdAndBoardId(memberId: Long, boardId: Long): Boolean
    fun deleteByMemberIdAndBoardId(memberId: Long, boardId: Long)
    fun findAllByMemberId(memberId: Long): List<LikeBoardEntity>
    fun findAllByMemberIdWithBoardInfo(memberId: Long, pageable: Pageable): Page<LikeBoardInfoResult>
}
