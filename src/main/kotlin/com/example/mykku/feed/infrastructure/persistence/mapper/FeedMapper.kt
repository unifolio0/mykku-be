package com.example.mykku.feed.infrastructure.persistence.mapper

import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.model.FeedContent
import com.example.mykku.feed.domain.model.FeedDomain
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
class FeedMapper {

    fun toDomain(entity: Feed): FeedDomain {
        return FeedDomain.reconstitute(
            id = FeedId(entity.id!!),
            title = entity.title,
            content = FeedContent(entity.content),
            boardId = BoardId(entity.board.id!!),
            authorId = MemberId(entity.member.id),
            likeCount = entity.likeCount,
            commentCount = entity.commentCount,
            createdAt = entity.createdAt.atZone(ZoneId.systemDefault()).toInstant(),
            updatedAt = entity.updatedAt.atZone(ZoneId.systemDefault()).toInstant()
        )
    }
}
