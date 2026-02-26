package com.example.mykku.notification.domain.vo

enum class NotificationType(val description: String) {
    FEED_LIKE("피드 좋아요"),
    FEED_COMMENT("피드 댓글"),
    SYSTEM_NOTICE("시스템 공지"),
    ROLE_EARNED("칭호 획득"),
    CONTEST("콘테스트"),
    EVENT("이벤트")
}
