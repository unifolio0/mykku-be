package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.FcmTokenQueryPort
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.google.firebase.messaging.Notification as FcmNotification

@Service
class FcmService(
    private val fcmTokenQueryPort: FcmTokenQueryPort
) {

    private val logger = LoggerFactory.getLogger(FcmService::class.java)

    @Async
    fun sendNotificationToMember(
        member: Member,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) {
        val tokens = fcmTokenQueryPort.getTokensByMember(member)
        if (tokens.isEmpty()) {
            logger.info("No FCM tokens found for member: ${member.id}")
            return
        }

        try {
            val tokenStrings = tokens.map { it.token }

            val notification = FcmNotification.builder()
                .setTitle(title)
                .setBody(body)
                .build()

            val message = MulticastMessage.builder()
                .addAllTokens(tokenStrings)
                .setNotification(notification)
                .putAllData(data)
                .build()

            val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
            logger.info("Successfully sent ${response.successCount}/${tokenStrings.size} FCM messages to member: ${member.id}")

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
            logger.error("Failed to send FCM messages to member: ${member.id}", e)
        }
    }
}
