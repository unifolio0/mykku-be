package com.example.mykku.notification.exception

import com.example.mykku.common.exception.BaseDomainException

class NotificationException(
    errorCode: NotificationErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun notificationNotFound(): NotificationException =
            NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND)

        fun notificationNotAuthorized(): NotificationException =
            NotificationException(NotificationErrorCode.NOTIFICATION_NOT_AUTHORIZED)

        fun fcmTokenNotFound(): NotificationException =
            NotificationException(NotificationErrorCode.FCM_TOKEN_NOT_FOUND)

        fun fcmTokenInvalid(): NotificationException =
            NotificationException(NotificationErrorCode.FCM_TOKEN_INVALID)

        fun fcmSendFailed(cause: Throwable? = null): NotificationException =
            NotificationException(NotificationErrorCode.FCM_SEND_FAILED, cause = cause)

        fun notificationSettingNotFound(): NotificationException =
            NotificationException(NotificationErrorCode.NOTIFICATION_SETTING_NOT_FOUND)

        fun invalidNotificationType(): NotificationException =
            NotificationException(NotificationErrorCode.INVALID_NOTIFICATION_TYPE)
    }
}
