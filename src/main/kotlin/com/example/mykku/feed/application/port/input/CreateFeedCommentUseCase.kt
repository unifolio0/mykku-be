package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.CreateFeedCommentCommand
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.member.domain.Member

interface CreateFeedCommentUseCase {
    fun execute(command: CreateFeedCommentCommand, member: Member): SingleFeedCommentResult
}
