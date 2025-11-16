package com.example.mykku.notification.event

import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.domain.NotificationType
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationEventListener(
    private val notificationService: NotificationService
) {

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFeedLikedEvent(event: FeedLikedEvent) {
        if (event.feedAuthor.id == event.liker.id) {
            return
        }

        notificationService.createAndSendNotification(
            type = NotificationType.FEED_LIKE,
            sender = event.liker,
            receiver = event.feedAuthor,
            content = "${event.liker.nickname}님이 회원님의 피드를 좋아합니다.",
            relatedResourceId = event.feedId,
            relatedResourceType = "FEED"
        )
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFeedCommentedEvent(event: FeedCommentedEvent) {
        if (event.feedAuthor.id == event.commenter.id) {
            return
        }

        notificationService.createAndSendNotification(
            type = NotificationType.FEED_COMMENT,
            sender = event.commenter,
            receiver = event.feedAuthor,
            content = "${event.commenter.nickname}님이 회원님의 피드에 댓글을 남겼습니다: ${event.commentContent}",
            relatedResourceId = event.feedId,
            relatedResourceType = "FEED"
        )
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFollowedEvent(event: FollowedEvent) {
        notificationService.createAndSendNotification(
            type = NotificationType.FOLLOW,
            sender = event.follower,
            receiver = event.following,
            content = "${event.follower.nickname}님이 회원님을 팔로우했습니다.",
            relatedResourceId = null,
            relatedResourceType = "MEMBER"
        )
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFeedCreatedByFollowingEvent(event: FeedCreatedByFollowingEvent) {
        event.followers.forEach { follower ->
            notificationService.createAndSendNotification(
                type = NotificationType.FOLLOWING_POST,
                sender = event.author,
                receiver = follower,
                content = "${event.author.nickname}님이 새 게시글을 작성했습니다: ${event.feedTitle}",
                relatedResourceId = event.feedId,
                relatedResourceType = "FEED"
            )
        }
    }
}
