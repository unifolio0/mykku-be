package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.dto.UpdateFeedCommentCommand
import com.example.mykku.member.domain.Member

interface UpdateFeedCommentUseCase {
    fun execute(command: UpdateFeedCommentCommand, member: Member): SingleFeedCommentResult
}
