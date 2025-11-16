package com.example.mykku.notification.event

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.domain.NotificationType
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import kotlin.test.assertTrue

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
            type = eq(NotificationType.FEED_COMMENT),
            sender = eq(author),
            receiver = eq(author),
            content = eq("${author.nickname}님이 회원님의 피드에 댓글을 남겼습니다: 추가 설명입니다"),
            relatedResourceId = eq(1L),
            relatedResourceType = eq("FEED")
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
            relatedResourceId = null,
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
            type = eq(NotificationType.FOLLOWING_POST),
            sender = eq(author),
            receiver = any(),
            content = any(),
            relatedResourceId = any(),
            relatedResourceType = any()
        )
    }

    @Test
    fun `handleFeedCommentedEvent는 긴 댓글 내용을 500자로 제한한다`() {
        val longComment = "정말 멋진 피드네요! 저도 이런 걸 만들어보고 싶어요. ".repeat(50)
        val event = FeedCommentedEvent(
            feedId = 1L,
            feedAuthor = author,
            commenter = commenter,
            commentContent = longComment
        )

        notificationEventListener.handleFeedCommentedEvent(event)

        argumentCaptor<String>().apply {
            verify(notificationService).createAndSendNotification(
                type = eq(NotificationType.FEED_COMMENT),
                sender = eq(commenter),
                receiver = eq(author),
                content = capture(),
                relatedResourceId = eq(1L),
                relatedResourceType = eq("FEED")
            )

            assertTrue(firstValue.length <= 500, "Content length ${firstValue.length} exceeds 500")
            assertTrue(firstValue.endsWith("..."), "Long content should end with ellipsis")
            assertTrue(firstValue.startsWith("${commenter.nickname}님이 회원님의 피드에 댓글을 남겼습니다: "))
        }
    }

    @Test
    fun `handleFeedCreatedByFollowingEvent는 긴 피드 제목을 500자로 제한한다`() {
        val longTitle = "오늘 아침에 본 정말 아름다운 일출 사진입니다 모두 보세요! ".repeat(20)
        val event = FeedCreatedByFollowingEvent(
            feedId = 1L,
            feedTitle = longTitle,
            author = author,
            followers = listOf(follower)
        )

        notificationEventListener.handleFeedCreatedByFollowingEvent(event)

        argumentCaptor<String>().apply {
            verify(notificationService).createAndSendNotification(
                type = eq(NotificationType.FOLLOWING_POST),
                sender = eq(author),
                receiver = eq(follower),
                content = capture(),
                relatedResourceId = eq(1L),
                relatedResourceType = eq("FEED")
            )

            assertTrue(firstValue.length <= 500, "Content length ${firstValue.length} exceeds 500")
            assertTrue(firstValue.endsWith("..."), "Long content should end with ellipsis")
            assertTrue(firstValue.startsWith("${author.nickname}님이 새 게시글을 작성했습니다: "))
        }
    }
}
