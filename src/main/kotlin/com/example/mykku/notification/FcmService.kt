package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.tool.FcmTokenReader
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.google.firebase.messaging.Notification as FcmNotification

@Service
class FcmService(
    private val fcmTokenReader: FcmTokenReader
) {

    private val logger = LoggerFactory.getLogger(FcmService::class.java)

    @Async
    fun sendNotificationToMember(
        member: Member,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) {
        val tokens = fcmTokenReader.getTokensByMember(member)
        if (tokens.isEmpty()) {
            logger.info("No FCM tokens found for member: ${member.id}")
            return
        }

        tokens.forEach { token ->
            sendNotificationToToken(token, title, body, data)
        }
    }

    private fun sendNotificationToToken(
        fcmToken: FcmToken,
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        try {
            val notification = FcmNotification.builder()
                .setTitle(title)
                .setBody(body)
                .build()

            val message = Message.builder()
                .setToken(fcmToken.token)
                .setNotification(notification)
                .putAllData(data)
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Successfully sent FCM message: $response")
        } catch (e: Exception) {
            logger.error("Failed to send FCM message to token: ${fcmToken.deviceId}", e)
        }
    }
}
