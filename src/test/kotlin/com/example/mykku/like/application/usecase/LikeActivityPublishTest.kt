package com.example.mykku.like.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.like.application.dto.LikeDailyMessageCommentCommand
import com.example.mykku.like.application.dto.LikeFeedCommand
import com.example.mykku.like.application.dto.LikeFeedCommentCommand
import com.example.mykku.like.application.dto.UnlikeFeedCommand
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.like.domain.entity.LikeFeedEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("좋아요 활동 이벤트 발행 단위 테스트")
class LikeActivityPublishTest {

    @Mock
    private lateinit var likeFeedPort: LikeFeedPort

    @Mock
    private lateinit var likeFeedCommentPort: LikeFeedCommentPort

    @Mock
    private lateinit var likeDailyMessageCommentPort: LikeDailyMessageCommentPort

    @Mock
    private lateinit var activityEventPublisher: ActivityEventPublisher

    @Test
    @DisplayName("피드 좋아요는 LIKE_PRESS 이벤트를 발행한다")
    fun likeFeedPublishes() {
        val service = LikeFeedService(likeFeedPort, activityEventPublisher)
        whenever(likeFeedPort.existsByMemberIdAndFeedId(1L, 2L)).thenReturn(false)
        whenever(likeFeedPort.save(any())).thenReturn(savedLikeFeed())

        service.likeFeed(LikeFeedCommand(memberId = 1L, feedId = 2L))

        verify(activityEventPublisher).publish(ActivityEvent(1L, ActivityType.LIKE_PRESS))
    }

    @Test
    @DisplayName("피드 좋아요 취소는 이벤트를 발행하지 않는다")
    fun unlikeFeedDoesNotPublish() {
        val service = LikeFeedService(likeFeedPort, activityEventPublisher)
        whenever(likeFeedPort.existsByMemberIdAndFeedId(1L, 2L)).thenReturn(true)

        service.unlikeFeed(UnlikeFeedCommand(memberId = 1L, feedId = 2L))

        verify(activityEventPublisher, never()).publish(any())
    }

    @Test
    @DisplayName("피드 댓글 좋아요는 LIKE_PRESS 이벤트를 발행한다")
    fun likeFeedCommentPublishes() {
        val service = LikeFeedCommentService(likeFeedCommentPort, activityEventPublisher)
        whenever(likeFeedCommentPort.existsByMemberIdAndFeedCommentId(1L, 3L)).thenReturn(false)
        whenever(likeFeedCommentPort.save(any())).thenReturn(savedLikeFeedComment())

        service.likeFeedComment(LikeFeedCommentCommand(memberId = 1L, feedCommentId = 3L))

        verify(activityEventPublisher).publish(ActivityEvent(1L, ActivityType.LIKE_PRESS))
    }

    @Test
    @DisplayName("하루덕담 댓글 좋아요는 LIKE_PRESS 이벤트를 발행한다")
    fun likeDailyMessageCommentPublishes() {
        val service = LikeDailyMessageCommentService(likeDailyMessageCommentPort, activityEventPublisher)
        whenever(likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(1L, 4L)).thenReturn(false)
        whenever(likeDailyMessageCommentPort.save(any())).thenReturn(savedLikeDailyMessageComment())

        service.likeDailyMessageComment(
            LikeDailyMessageCommentCommand(memberId = 1L, dailyMessageCommentId = 4L)
        )

        verify(activityEventPublisher).publish(ActivityEvent(1L, ActivityType.LIKE_PRESS))
    }

    private fun savedLikeFeed(): LikeFeedEntity =
        LikeFeedEntity.reconstitute(
            id = 10L,
            memberId = 1L,
            feedId = 2L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

    private fun savedLikeFeedComment(): LikeFeedCommentEntity =
        LikeFeedCommentEntity.reconstitute(
            id = 11L,
            memberId = 1L,
            feedCommentId = 3L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

    private fun savedLikeDailyMessageComment(): LikeDailyMessageCommentEntity =
        LikeDailyMessageCommentEntity.reconstitute(
            id = 12L,
            memberId = 1L,
            dailyMessageCommentId = 4L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
}
