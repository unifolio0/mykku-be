package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.DeleteFeedCommentCommand
import com.example.mykku.member.domain.Member

interface DeleteFeedCommentUseCase {
    fun execute(command: DeleteFeedCommentCommand, member: Member)
}
