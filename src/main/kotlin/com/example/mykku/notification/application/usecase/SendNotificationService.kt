package com.example.mykku.notification.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.notification.application.dto.CreateNotificationCommand
import com.example.mykku.notification.application.dto.NotificationResult
import com.example.mykku.notification.application.port.input.SendNotificationUseCase
import com.example.mykku.notification.application.port.output.FcmTokenRepository
import com.example.mykku.notification.application.port.output.NotificationRepository
import com.example.mykku.notification.application.port.output.NotificationSettingRepository
import com.example.mykku.notification.domain.entity.Notification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.google.firebase.messaging.Notification as FcmNotification

@Service
class SendNotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationSettingRepository: NotificationSettingRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val memberRepository: MemberRepository
) : SendNotificationUseCase {

    private val logger = LoggerFactory.getLogger(SendNotificationService::class.java)

    @Transactional
    override fun createAndSendNotification(command: CreateNotificationCommand): NotificationResult? {
        val setting = notificationSettingRepository.findByMemberIdAndNotificationType(
            command.receiverId,
            command.type
        )
        if (setting != null && !setting.isEnabled) {
            return null
        }

        val notification = Notification.create(
            type = command.type,
            senderId = command.senderId,
            receiverId = command.receiverId,
            content = command.content,
            relatedResourceId = command.relatedResourceId,
            relatedResourceType = command.relatedResourceType,
            displayColor = command.displayColor
        )

        val savedNotification = notificationRepository.save(notification)

        sendFcmNotification(command)

        val sender = command.senderId?.let { memberRepository.findByIdString(it) }
        return NotificationResult.from(savedNotification, sender?.nickname, sender?.profileImage)
    }

    @Async
    fun sendFcmNotification(command: CreateNotificationCommand) {
        val tokens = fcmTokenRepository.findAllByMemberId(command.receiverId)
        if (tokens.isEmpty()) {
            logger.info("No FCM tokens found for member: ${command.receiverId}")
            return
        }

        try {
            val tokenStrings = tokens.map { it.token }

            val notification = FcmNotification.builder()
                .setTitle(command.type.description)
                .setBody(command.content)
                .build()

            val message = MulticastMessage.builder()
                .addAllTokens(tokenStrings)
                .setNotification(notification)
                .putAllData(
                    mapOf(
                        "type" to command.type.name,
                        "relatedResourceId" to (command.relatedResourceId?.toString() ?: ""),
                        "relatedResourceType" to (command.relatedResourceType ?: "")
                    )
                )
                .build()

            val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
            logger.info("Successfully sent ${response.successCount}/${tokenStrings.size} FCM messages to member: ${command.receiverId}")

            if (response.failureCount > 0) {
                response.responses.forEachIndexed { index, sendResponse ->
                    if (!sendResponse.isSuccessful) {
                        logger.error(
                            "Failed to send FCM message to device: ${tokens[index].deviceId}, error: ${sendResponse.exception?.message}"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to send FCM messages to member: ${command.receiverId}", e)
        }
    }
}
