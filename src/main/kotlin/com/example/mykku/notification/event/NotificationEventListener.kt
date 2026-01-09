package com.example.mykku.notification.event

import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.util.NotificationContentUtil
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
            content = NotificationContentUtil.buildContent(
                "%s님이 회원님의 피드에 댓글을 남겼습니다: ",
                event.commentContent,
                event.commenter.nickname
            ),
            relatedResourceId = event.feedId,
            relatedResourceType = "FEED"
        )
    }
}
