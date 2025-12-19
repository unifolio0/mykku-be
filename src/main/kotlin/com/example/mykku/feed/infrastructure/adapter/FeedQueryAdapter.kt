package com.example.mykku.feed.infrastructure.adapter

import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.application.port.out.FeedSummary
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.feed.repository.FeedRepository
import org.springframework.stereotype.Component

@Component
class FeedQueryAdapter(
    private val feedRepository: FeedRepository
) : FeedQueryPort {

    override fun findSummaryById(id: FeedId): FeedSummary? {
        return feedRepository.findById(id.value)
            .map { feed ->
                FeedSummary(
                    id = feed.id!!,
                    title = feed.title,
                    authorId = feed.member.id,
                    boardId = feed.board.id!!
                )
            }
            .orElse(null)
    }

    override fun existsById(id: FeedId): Boolean {
        return feedRepository.existsById(id.value)
    }
}
