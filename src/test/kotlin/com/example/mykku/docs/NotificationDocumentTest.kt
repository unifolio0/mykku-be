package com.example.mykku.docs

import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationResponse
import com.example.mykku.notification.exception.NotificationErrorCode
import com.example.mykku.notification.exception.NotificationException
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class NotificationDocumentTest : BaseDocumentTest() {

    @Test
    fun `알림 목록 조회`() {
        val notifications = listOf(
            NotificationResponse(
                id = 1L,
                type = NotificationType.FEED_LIKE,
                senderNickname = "홍길동",
                senderProfileImage = "https://example.com/profile.jpg",
                content = "홍길동님이 회원님의 피드를 좋아합니다",
                isRead = false,
                relatedResourceId = 123L,
                relatedResourceType = "FEED",
                createdAt = LocalDateTime.now()
            ),
            NotificationResponse(
                id = 2L,
                type = NotificationType.FEED_COMMENT,
                senderNickname = "김철수",
                senderProfileImage = null,
                content = "김철수님이 회원님의 피드에 댓글을 남겼습니다",
                isRead = true,
                relatedResourceId = 123L,
                relatedResourceType = "FEED",
                createdAt = LocalDateTime.now().minusDays(1)
            )
        )
        val page = PageImpl(notifications, PageRequest.of(0, 20), 2)

        `when`(notificationService.getNotifications(any(), any())).thenReturn(page)

        val documentFilter = document("notification/list", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("알림 목록 조회")
                    .description("사용자의 알림 목록을 페이지네이션으로 조회합니다.")
                    .queryParameter(
                        parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("알림 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("알림 ID"),
                        fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("알림 타입"),
                        fieldWithPath("data.content[].senderNickname").type(JsonFieldType.STRING).description("발신자 닉네임")
                            .optional(),
                        fieldWithPath("data.content[].senderProfileImage").type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지").optional(),
                        fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("알림 내용"),
                        fieldWithPath("data.content[].isRead").type(JsonFieldType.BOOLEAN).description("읽음 여부"),
                        fieldWithPath("data.content[].relatedResourceId").type(JsonFieldType.NUMBER)
                            .description("관련 리소스 ID").optional(),
                        fieldWithPath("data.content[].relatedResourceType").type(JsonFieldType.STRING)
                            .description("관련 리소스 타입").optional(),
                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("알림 생성 일시"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("정렬 정보 비어있음 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .param("page", "0")
            .param("size", "20")
            .`when`()
            .get("/api/v1/notifications")
            .then()
            .statusCode(200)
    }

    @Test
    fun `읽지 않은 알림 목록 조회`() {
        val notifications = listOf(
            NotificationResponse(
                id = 1L,
                type = NotificationType.FEED_LIKE,
                senderNickname = "홍길동",
                senderProfileImage = "https://example.com/profile.jpg",
                content = "홍길동님이 회원님의 피드를 좋아합니다",
                isRead = false,
                relatedResourceId = 123L,
                relatedResourceType = "FEED",
                createdAt = LocalDateTime.now()
            ),
            NotificationResponse(
                id = 2L,
                type = NotificationType.FEED_COMMENT,
                senderNickname = "김철수",
                senderProfileImage = null,
                content = "김철수님이 회원님의 피드에 댓글을 남겼습니다",
                isRead = false,
                relatedResourceId = 124L,
                relatedResourceType = "FEED",
                createdAt = LocalDateTime.now().minusHours(1)
            )
        )
        val page = PageImpl(notifications, PageRequest.of(0, 20), 2)

        `when`(notificationService.getUnreadNotifications(any(), any())).thenReturn(page)

        val documentFilter = document("notification/unread-list", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("읽지 않은 알림 목록 조회")
                    .description("읽지 않은 알림 목록을 페이지네이션으로 조회합니다.")
                    .queryParameter(
                        parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("읽지 않은 알림 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("알림 ID"),
                        fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("알림 타입"),
                        fieldWithPath("data.content[].senderNickname").type(JsonFieldType.STRING).description("발신자 닉네임")
                            .optional(),
                        fieldWithPath("data.content[].senderProfileImage").type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지").optional(),
                        fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("알림 내용"),
                        fieldWithPath("data.content[].isRead").type(JsonFieldType.BOOLEAN).description("읽음 여부"),
                        fieldWithPath("data.content[].relatedResourceId").type(JsonFieldType.NUMBER)
                            .description("관련 리소스 ID").optional(),
                        fieldWithPath("data.content[].relatedResourceType").type(JsonFieldType.STRING)
                            .description("관련 리소스 타입").optional(),
                        fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("알림 생성 일시"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("정렬 정보 비어있음 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .param("page", "0")
            .param("size", "20")
            .`when`()
            .get("/api/v1/notifications/unread")
            .then()
            .statusCode(200)
    }

    @Test
    fun `읽지 않은 알림 개수 조회`() {
        `when`(notificationService.getUnreadCount(any())).thenReturn(5L)

        val documentFilter = document("notification/unread-count", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("읽지 않은 알림 개수 조회")
                    .description("읽지 않은 알림의 개수를 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER).description("읽지 않은 알림 개수")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/notifications/unread/count")
            .then()
            .statusCode(200)
    }

    @Test
    fun `알림 읽음 처리`() {
        val notificationId = 1L

        val documentFilter = document("notification/mark-read", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("알림 읽음 처리")
                    .description("특정 알림을 읽음 처리합니다.")
                    .pathParameter(
                        parameterWithName("notificationId").description("읽음 처리할 알림 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .patch("/api/v1/notifications/{notificationId}/read", notificationId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `모든 알림 읽음 처리`() {
        val documentFilter = document("notification/mark-all-read", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("모든 알림 읽음 처리")
                    .description("모든 알림을 읽음 처리합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .patch("/api/v1/notifications/read-all")
            .then()
            .statusCode(200)
    }

    @Test
    fun `알림 삭제`() {
        val notificationId = 1L

        val documentFilter = document("notification/delete", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("알림 삭제")
                    .description("특정 알림을 삭제합니다.")
                    .pathParameter(
                        parameterWithName("notificationId").description("삭제할 알림 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/notifications/{notificationId}", notificationId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `알림 삭제 - 알림을 찾을 수 없음`() {
        val notificationId = 999L

        doThrow(NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND))
            .`when`(notificationService).deleteNotification(any(), any())

        val documentFilter = document("notification/delete", "NOTIFICATION_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("알림 삭제 - 알림을 찾을 수 없음")
                    .description("존재하지 않는 알림을 삭제하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("notificationId").description("삭제할 알림 ID")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/notifications/{notificationId}", notificationId)
            .then()
            .statusCode(404)
    }

    @Test
    fun `알림 삭제 - 권한 없음`() {
        val notificationId = 1L

        doThrow(NotificationException(NotificationErrorCode.NOTIFICATION_NOT_AUTHORIZED))
            .`when`(notificationService).deleteNotification(any(), any())

        val documentFilter = document("notification/delete", "NOTIFICATION_NOT_AUTHORIZED")
            .request(
                request()
                    .tag(Tag.NOTIFICATION_API)
                    .summary("알림 삭제 - 권한 없음")
                    .description("해당 알림을 삭제할 권한이 없을 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("notificationId").description("삭제할 알림 ID")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/notifications/{notificationId}", notificationId)
            .then()
            .statusCode(403)
    }
}
