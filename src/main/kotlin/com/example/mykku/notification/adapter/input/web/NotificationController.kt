package com.example.mykku.notification.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.dto.DeleteNotificationCommand
import com.example.mykku.notification.application.dto.GetNotificationsQuery
import com.example.mykku.notification.application.dto.GetUnreadCountQuery
import com.example.mykku.notification.application.dto.GetUnreadNotificationsQuery
import com.example.mykku.notification.application.dto.MarkAllAsReadCommand
import com.example.mykku.notification.application.dto.MarkAsReadCommand
import com.example.mykku.notification.application.port.input.DeleteNotificationUseCase
import com.example.mykku.notification.application.port.input.GetNotificationsUseCase
import com.example.mykku.notification.application.port.input.MarkNotificationReadUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) {

    @GetMapping
    fun getNotifications(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<NotificationResponse>>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )

        val query = GetNotificationsQuery(member.id, pageable)
        val notifications = getNotificationsUseCase.getNotifications(query)

        return ResponseEntity.ok(
            ApiResponse(
                message = "알림 목록을 성공적으로 조회했습니다.",
                data = notifications.map { NotificationResponse.from(it) }
            )
        )
    }

    @GetMapping("/unread")
    fun getUnreadNotifications(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<NotificationResponse>>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )

        val query = GetUnreadNotificationsQuery(member.id, pageable)
        val notifications = getNotificationsUseCase.getUnreadNotifications(query)

        return ResponseEntity.ok(
            ApiResponse(
                message = "읽지 않은 알림 목록을 성공적으로 조회했습니다.",
                data = notifications.map { NotificationResponse.from(it) }
            )
        )
    }

    @GetMapping("/unread/count")
    fun getUnreadCount(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Long>> {
        val query = GetUnreadCountQuery(member.id)
        val count = getNotificationsUseCase.getUnreadCount(query)

        return ResponseEntity.ok(
            ApiResponse(
                message = "읽지 않은 알림 개수를 성공적으로 조회했습니다.",
                data = count
            )
        )
    }

    @PatchMapping("/{notificationId}/read")
    fun markAsRead(
        @PathVariable notificationId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = MarkAsReadCommand(notificationId, member.id)
        markNotificationReadUseCase.markAsRead(command)

        return ResponseEntity.ok(
            ApiResponse(message = "알림을 읽음 처리했습니다.", data = Unit)
        )
    }

    @PatchMapping("/read-all")
    fun markAllAsRead(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = MarkAllAsReadCommand(member.id)
        markNotificationReadUseCase.markAllAsRead(command)

        return ResponseEntity.ok(
            ApiResponse(message = "모든 알림을 읽음 처리했습니다.", data = Unit)
        )
    }

    @DeleteMapping("/{notificationId}")
    fun deleteNotification(
        @PathVariable notificationId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = DeleteNotificationCommand(notificationId, member.id)
        deleteNotificationUseCase.deleteNotification(command)

        return ResponseEntity.ok(
            ApiResponse(message = "알림을 삭제했습니다.", data = Unit)
        )
    }
}
