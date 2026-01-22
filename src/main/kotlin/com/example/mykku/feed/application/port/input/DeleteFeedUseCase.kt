package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.DeleteFeedCommand
import com.example.mykku.member.domain.Member

interface DeleteFeedUseCase {
    fun execute(command: DeleteFeedCommand, member: Member)
}
