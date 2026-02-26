package com.example.mykku.notification.application.usecase

import com.example.mykku.notification.application.dto.CreateNotificationCommand
import com.example.mykku.notification.application.port.output.FcmTokenRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.google.firebase.messaging.Notification as FcmNotification

@Service
class FcmNotificationSender(
    private val fcmTokenRepository: FcmTokenRepository
) {

    private val logger = LoggerFactory.getLogger(FcmNotificationSender::class.java)

    @Async
    fun send(command: CreateNotificationCommand) {
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
            logger.info(
                "Successfully sent ${response.successCount}/${tokenStrings.size} " +
                "FCM messages to member: ${command.receiverId}"
            )

            if (response.failureCount > 0) {
                response.responses.forEachIndexed { index, sendResponse ->
                    if (!sendResponse.isSuccessful) {
                        logger.error(
                            "Failed to send FCM message to device: ${tokens[index].deviceId}, " +
                            "error: ${sendResponse.exception?.message}"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to send FCM messages to member: ${command.receiverId}", e)
        }
    }
}
