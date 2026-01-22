package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.feed.application.dto.UpdateFeedCommand
import com.example.mykku.member.domain.Member

interface UpdateFeedUseCase {
    fun execute(command: UpdateFeedCommand, member: Member): FeedDetailResult
}
