package com.example.mykku.notification.util

object NotificationContentUtil {
    private const val MAX_CONTENT_LENGTH = 500
    private const val ELLIPSIS = "..."

    fun buildContent(template: String, userContent: String, vararg args: Any): String {
        val formattedTemplate = template.format(*args)
        val maxUserContentLength = MAX_CONTENT_LENGTH - formattedTemplate.length

        return when {
            formattedTemplate.length + userContent.length <= MAX_CONTENT_LENGTH -> {
                formattedTemplate + userContent
            }
            else -> {
                val availableSpace = maxUserContentLength - ELLIPSIS.length
                formattedTemplate + userContent.take(availableSpace) + ELLIPSIS
            }
        }
    }
}
