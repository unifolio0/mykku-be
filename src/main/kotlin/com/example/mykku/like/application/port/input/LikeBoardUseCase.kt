package com.example.mykku.like.application.port.input

import com.example.mykku.like.application.dto.GetLikedBoardsQuery
import com.example.mykku.like.application.dto.LikeBoardCommand
import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.application.dto.LikeBoardResult
import com.example.mykku.like.application.dto.UnlikeBoardCommand
import org.springframework.data.domain.Page

interface LikeBoardUseCase {
    fun likeBoard(command: LikeBoardCommand): LikeBoardResult
    fun unlikeBoard(command: UnlikeBoardCommand)
    fun getLikedBoards(query: GetLikedBoardsQuery): Page<LikeBoardInfoResult>
}
