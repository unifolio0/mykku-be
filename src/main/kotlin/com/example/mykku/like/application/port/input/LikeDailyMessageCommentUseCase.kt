package com.example.mykku.like.application.port.input

import com.example.mykku.like.application.dto.LikeDailyMessageCommentCommand
import com.example.mykku.like.application.dto.LikeDailyMessageCommentResult
import com.example.mykku.like.application.dto.UnlikeDailyMessageCommentCommand

interface LikeDailyMessageCommentUseCase {
    fun likeDailyMessageComment(command: LikeDailyMessageCommentCommand): LikeDailyMessageCommentResult
    fun unlikeDailyMessageComment(command: UnlikeDailyMessageCommentCommand)
}
