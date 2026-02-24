package com.example.mykku.notification.domain.vo

enum class NotificationCategory(val description: String) {
    NOTICE("공지사항"),
    COMMUNITY("커뮤니티"),
    CONTENTS("콘텐츠");

    val types: List<NotificationType>
        get() = when (this) {
            NOTICE -> listOf(NotificationType.SYSTEM_NOTICE)
            COMMUNITY -> listOf(NotificationType.FEED_LIKE, NotificationType.FEED_COMMENT, NotificationType.ROLE_EARNED)
            CONTENTS -> listOf(NotificationType.CONTEST, NotificationType.EVENT)
        }

    companion object {
        fun fromType(type: NotificationType): NotificationCategory {
            return entries.first { type in it.types }
        }
    }
}
