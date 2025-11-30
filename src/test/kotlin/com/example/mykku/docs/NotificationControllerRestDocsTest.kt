package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.notification.NotificationController
import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationResponse
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class NotificationControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var notificationService: NotificationService

    @InjectMocks
    private lateinit var notificationController: NotificationController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(notificationController)
    }

    @Test
    fun `알림 목록 조회 API 문서화`() {
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
                createdAt = LocalDateTime.of(2024, 11, 16, 14, 30)
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
                createdAt = LocalDateTime.of(2024, 11, 15, 10, 15)
            )
        )
        val page = PageImpl(notifications, PageRequest.of(0, 20), 2)

        `when`(notificationService.getNotifications(any(), any())).thenReturn(page)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/notifications")
                .param("page", "0")
                .param("size", "20")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("알림 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "notification-list",
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("알림 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("알림 ID"),
                        fieldWithPath("data.content[].type").type(JsonFieldType.STRING)
                            .description("알림 타입 (FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE)"),
                        fieldWithPath("data.content[].senderNickname").type(JsonFieldType.STRING)
                            .description("발신자 닉네임").optional(),
                        fieldWithPath("data.content[].senderProfileImage").type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지 URL").optional(),
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
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
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
            )
    }

    @Test
    fun `읽지 않은 알림 개수 조회 API 문서화`() {
        `when`(notificationService.getUnreadCount(any())).thenReturn(5L)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/notifications/unread/count")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("읽지 않은 알림 개수를 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "notification-unread-count",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER).description("읽지 않은 알림 개수")
                    )
                )
            )
    }

    @Test
    fun `알림 읽음 처리 API 문서화`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/notifications/{notificationId}/read", 1L)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("알림을 읽음 처리했습니다."))
            .andDo(
                document(
                    "notification-mark-as-read",
                    pathParameters(
                        parameterWithName("notificationId").description("읽음 처리할 알림 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (null)")
                    )
                )
            )
    }

    @Test
    fun `모든 알림 읽음 처리 API 문서화`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/notifications/read-all")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("모든 알림을 읽음 처리했습니다."))
            .andDo(
                document(
                    "notification-mark-all-as-read",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (null)")
                    )
                )
            )
    }

    @Test
    fun `알림 삭제 API 문서화`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/notifications/{notificationId}", 1L)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("알림을 삭제했습니다."))
            .andDo(
                document(
                    "notification-delete",
                    pathParameters(
                        parameterWithName("notificationId").description("삭제할 알림 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (null)")
                    )
                )
            )
    }
}
