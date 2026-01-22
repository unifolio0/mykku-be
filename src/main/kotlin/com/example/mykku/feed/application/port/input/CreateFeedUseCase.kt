package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.CreateFeedCommand
import com.example.mykku.feed.application.dto.CreateFeedResult
import com.example.mykku.member.domain.Member

interface CreateFeedUseCase {
    fun execute(command: CreateFeedCommand, member: Member): CreateFeedResult
}
