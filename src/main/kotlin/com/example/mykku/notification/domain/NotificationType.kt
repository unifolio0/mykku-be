package com.example.mykku.notification.domain

enum class NotificationType(val description: String) {
    FEED_LIKE("피드 좋아요"),
    FEED_COMMENT("피드 댓글"),
    SYSTEM_NOTICE("시스템 공지")
}
