package com.example.mykku.notification.event

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.domain.NotificationType
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class NotificationEventListenerTest : BaseToolTest() {

    @Mock
    private lateinit var notificationService: NotificationService

    @InjectMocks
    private lateinit var notificationEventListener: NotificationEventListener

    private val author = createMockMember("author1", "Author")
    private val liker = createMockMember("liker1", "Liker")
    private val commenter = createMockMember("commenter1", "Commenter")
    private val follower = createMockMember("follower1", "Follower")
    private val following = createMockMember("following1", "Following")

    @Test
    fun `handleFeedLikedEvent는 피드 좋아요 알림을 생성한다`() {
        val event = FeedLikedEvent(
            feedId = 1L,
            feedAuthor = author,
            liker = liker
        )

        notificationEventListener.handleFeedLikedEvent(event)

        verify(notificationService).createAndSendNotification(
            type = NotificationType.FEED_LIKE,
            sender = liker,
            receiver = author,
            content = "${liker.nickname}님이 회원님의 피드를 좋아합니다.",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )
    }

    @Test
    fun `handleFeedLikedEvent는 자신의 피드를 좋아요하면 알림을 생성하지 않는다`() {
        val event = FeedLikedEvent(
            feedId = 1L,
            feedAuthor = author,
            liker = author
        )

        notificationEventListener.handleFeedLikedEvent(event)

        verify(notificationService, never()).createAndSendNotification(
            type = NotificationType.FEED_LIKE,
            sender = author,
            receiver = author,
            content = "${author.nickname}님이 회원님의 피드를 좋아합니다.",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )
    }

    @Test
    fun `handleFeedCommentedEvent는 피드 댓글 알림을 생성한다`() {
        val event = FeedCommentedEvent(
            feedId = 1L,
            feedAuthor = author,
            commenter = commenter,
            commentContent = "좋은 글이네요!"
        )

        notificationEventListener.handleFeedCommentedEvent(event)

        verify(notificationService).createAndSendNotification(
            type = NotificationType.FEED_COMMENT,
            sender = commenter,
            receiver = author,
            content = "${commenter.nickname}님이 회원님의 피드에 댓글을 남겼습니다: 좋은 글이네요!",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )
    }

    @Test
    fun `handleFeedCommentedEvent는 자신의 피드에 댓글을 달면 알림을 생성하지 않는다`() {
        val event = FeedCommentedEvent(
            feedId = 1L,
            feedAuthor = author,
            commenter = author,
            commentContent = "추가 설명입니다"
        )

        notificationEventListener.handleFeedCommentedEvent(event)

        verify(notificationService, never()).createAndSendNotification(
            type = NotificationType.FEED_COMMENT,
            sender = author,
            receiver = author,
            content = "${author.nickname}님이 회원님의 피드에 댓글을 남겼습니다: 추가 설명입니다",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )
    }

    @Test
    fun `handleFollowedEvent는 팔로우 알림을 생성한다`() {
        val event = FollowedEvent(
            follower = follower,
            following = following
        )

        notificationEventListener.handleFollowedEvent(event)

        verify(notificationService).createAndSendNotification(
            type = NotificationType.FOLLOW,
            sender = follower,
            receiver = following,
            content = "${follower.nickname}님이 회원님을 팔로우했습니다.",
            relatedResourceId = follower.id.toLong(),
            relatedResourceType = "MEMBER"
        )
    }

    @Test
    fun `handleFeedCreatedByFollowingEvent는 팔로잉의 새 게시글 알림을 생성한다`() {
        val followers = listOf(follower, createMockMember("follower2", "Follower2"))
        val event = FeedCreatedByFollowingEvent(
            feedId = 1L,
            feedTitle = "새 게시글 제목",
            author = author,
            followers = followers
        )

        notificationEventListener.handleFeedCreatedByFollowingEvent(event)

        followers.forEach { followerMember ->
            verify(notificationService).createAndSendNotification(
                type = NotificationType.FOLLOWING_POST,
                sender = author,
                receiver = followerMember,
                content = "${author.nickname}님이 새 게시글을 작성했습니다: 새 게시글 제목",
                relatedResourceId = 1L,
                relatedResourceType = "FEED"
            )
        }
    }

    @Test
    fun `handleFeedCreatedByFollowingEvent는 팔로워가 없으면 알림을 생성하지 않는다`() {
        val event = FeedCreatedByFollowingEvent(
            feedId = 1L,
            feedTitle = "새 게시글 제목",
            author = author,
            followers = emptyList()
        )

        notificationEventListener.handleFeedCreatedByFollowingEvent(event)

        verify(notificationService, never()).createAndSendNotification(
            type = NotificationType.FOLLOWING_POST,
            sender = author,
            receiver = org.mockito.kotlin.any(),
            content = org.mockito.kotlin.any(),
            relatedResourceId = org.mockito.kotlin.any(),
            relatedResourceType = org.mockito.kotlin.any()
        )
    }
}
