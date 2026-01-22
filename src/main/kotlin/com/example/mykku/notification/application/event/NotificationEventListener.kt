package com.example.mykku.notification.application.event

import com.example.mykku.notification.application.dto.CreateNotificationCommand
import com.example.mykku.notification.application.port.input.SendNotificationUseCase
import com.example.mykku.notification.application.util.NotificationContentUtil
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationEventListener(
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFeedLikedEvent(event: FeedLikedEvent) {
        if (event.feedAuthorId == event.likerId) {
            return
        }

        val command = CreateNotificationCommand(
            type = NotificationType.FEED_LIKE,
            senderId = event.likerId,
            receiverId = event.feedAuthorId,
            content = "${event.likerNickname}님이 회원님의 피드를 좋아합니다.",
            relatedResourceId = event.feedId,
            relatedResourceType = "FEED"
        )

        sendNotificationUseCase.createAndSendNotification(command)
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFeedCommentedEvent(event: FeedCommentedEvent) {
        if (event.feedAuthorId == event.commenterId) {
            return
        }

        val command = CreateNotificationCommand(
            type = NotificationType.FEED_COMMENT,
            senderId = event.commenterId,
            receiverId = event.feedAuthorId,
            content = NotificationContentUtil.buildContent(
                "%s님이 회원님의 피드에 댓글을 남겼습니다: ",
                event.commentContent,
                event.commenterNickname
            ),
            relatedResourceId = event.feedId,
            relatedResourceType = "FEED"
        )

        sendNotificationUseCase.createAndSendNotification(command)
    }
}
