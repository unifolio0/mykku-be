package com.example.mykku.docs

import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.dto.ContestDetailResponse
import com.example.mykku.contest.dto.ContestImageRequest
import com.example.mykku.contest.dto.ContestImageResponse
import com.example.mykku.contest.dto.ContestListResponse
import com.example.mykku.contest.dto.CreateContestRequest
import com.example.mykku.contest.dto.CreateContestResponse
import com.example.mykku.contest.dto.PagedContestsResponse
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
import io.restassured.http.ContentType
import java.time.LocalDateTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class ContestDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("공모전 생성")
    inner class CreateContest {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_API,
            summary = "공모전 생성",
            description = "새로운 공모전을 생성합니다.",
            requestBodyFields = listOf(
                fieldWithPath("title").type(JsonFieldType.STRING).description("공모전 제목"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("공모전 설명").optional(),
                fieldWithPath("expiredAt").type(JsonFieldType.STRING)
                    .description("공모전 만료일 (yyyy-MM-dd'T'HH:mm:ss)"),
                fieldWithPath("startedAt").type(JsonFieldType.STRING)
                    .description("공모전 시작일 (yyyy-MM-dd'T'HH:mm:ss)"),
                fieldWithPath("images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록 (최대 10개)"),
                fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("공모전 태그 목록 (최대 7개)")
            )
        )

        @Test
        fun `성공`() {
            val request = CreateContestRequest(
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(
                    ContestImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0),
                    ContestImageRequest(url = "https://example.com/image2.jpg", orderIndex = 1)
                ),
                tags = listOf("디자인", "개발", "기획")
            )

            val response = CreateContestResponse(
                id = 1L,
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.of(2024, 6, 1, 0, 0, 0),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(
                    ContestImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                    ContestImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
                ),
                tags = listOf("디자인", "개발", "기획"),
                createdAt = LocalDateTime.now()
            )

            `when`(contestService.createContest(any())).thenReturn(response)

            val documentFilter = document("contest/create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 공모전 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("공모전 제목"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("공모전 설명")
                                .optional(),
                            fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("공모전 만료일"),
                            fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                            fieldWithPath("data.tags[]").type(JsonFieldType.ARRAY).description("공모전 태그 목록"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시"),
                            fieldWithPath("data.startedAt").type(JsonFieldType.STRING).description("시작일시")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/contests")
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미지 개수 초과`() {
            val images = (0..10).map { i ->
                ContestImageRequest(url = "https://example.com/image$i.jpg", orderIndex = i)
            }
            val request = CreateContestRequest(
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = images,
                tags = listOf("디자인")
            )

            `when`(contestService.createContest(any()))
                .thenThrow(ContestException(ContestErrorCode.CONTEST_IMAGE_LIMIT_EXCEEDED))

            val documentFilter = document("contest/create", "CONTEST_IMAGE_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/contests")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 개수 초과`() {
            val request = CreateContestRequest(
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(ContestImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0)),
                tags = listOf("태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7", "태그8")
            )

            `when`(contestService.createContest(any()))
                .thenThrow(ContestException(ContestErrorCode.CONTEST_TAG_LIMIT_EXCEEDED))

            val documentFilter = document("contest/create", "CONTEST_TAG_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/contests")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 길이 초과`() {
            val request = CreateContestRequest(
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(ContestImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0)),
                tags = listOf("가".repeat(21))
            )

            `when`(contestService.createContest(any()))
                .thenThrow(ContestException(ContestErrorCode.TAG_TITLE_TOO_LONG))

            val documentFilter = document("contest/create", "TAG_TITLE_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/contests")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 형식 오류`() {
            val request = CreateContestRequest(
                title = "신규 공모전",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(ContestImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0)),
                tags = listOf("태그!@#")
            )

            `when`(contestService.createContest(any()))
                .thenThrow(ContestException(ContestErrorCode.TAG_INVALID_FORMAT))

            val documentFilter = document("contest/create", "TAG_INVALID_FORMAT")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/contests")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("공모전 목록 조회")
    inner class GetContestList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_API,
            summary = "공모전 목록 조회",
            description = "공모전 목록을 조회합니다. 상태와 정렬 방식으로 필터링할 수 있습니다.",
            queryParameters = listOf(
                parameterWithName("status").description("공모전 상태 (ACTIVE, EXPIRED, ALL) 기본값: ACTIVE").optional(),
                parameterWithName("sortType").description("정렬 방식 (LATEST, OLDEST, POPULAR) 기본값: LATEST")
                    .optional(),
                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
            )
        )

        @Test
        fun `성공`() {
            val contestList = listOf(
                ContestListResponse(
                    id = 1L,
                    title = "첫 번째 공모전",
                    startedAt = LocalDateTime.now(),
                    expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                    status = ContestStatusType.ACTIVE,
                    thumbnailUrl = "https://example.com/thumbnail1.jpg",
                    tags = listOf("디자인", "개발"),
                    isSaved = true
                ),
                ContestListResponse(
                    id = 2L,
                    title = "두 번째 공모전",
                    startedAt = LocalDateTime.now(),
                    expiredAt = LocalDateTime.of(2025, 11, 30, 23, 59, 59),
                    status = ContestStatusType.ACTIVE,
                    thumbnailUrl = "https://example.com/thumbnail2.jpg",
                    tags = listOf("기획"),
                    isSaved = false
                )
            )

            val response = PagedContestsResponse(
                content = contestList,
                page = 0,
                size = 20,
                totalElements = 2,
                totalPages = 1,
                isLast = true
            )

            `when`(contestService.getContests(any(), any(), any(), any(), anyOrNull())).thenReturn(response)

            val documentFilter = document("contest/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("공모전 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("공모전 ID"),
                            fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("공모전 제목"),
                            fieldWithPath("data.content[].expiredAt").type(JsonFieldType.STRING).description("만료일"),
                            fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("공모전 상태"),
                            fieldWithPath("data.content[].startedAt").type(JsonFieldType.STRING).description("시작일"),
                            fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING)
                                .description("썸네일 이미지 URL").optional(),
                            fieldWithPath("data.content[].tags[]").type(JsonFieldType.ARRAY).description("태그 목록"),
                            fieldWithPath("data.content[].isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                            fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("status", "ACTIVE")
                .param("sortType", "LATEST")
                .`when`()
                .get("/api/v1/contests")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("공모전 상세 조회")
    inner class GetContestDetail {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_API,
            summary = "공모전 상세 조회",
            description = "특정 공모전의 상세 정보를 조회합니다.",
            pathParameters = listOf(
                parameterWithName("contestId").description("공모전 ID")
            )
        )

        @Test
        fun `성공`() {
            val contestId = 1L
            val response = ContestDetailResponse(
                id = contestId,
                title = "공모전 제목",
                description = "공모전 상세 설명입니다.",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                status = ContestStatusType.ACTIVE,
                images = listOf(
                    ContestImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                    ContestImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
                ),
                tags = listOf("디자인", "개발", "기획"),
                isSaved = true,
                createdAt = LocalDateTime.now()
            )

            `when`(contestService.getContestDetail(any(), anyOrNull())).thenReturn(response)

            val documentFilter = document("contest/detail", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("공모전 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("공모전 제목"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("공모전 설명")
                                .optional(),
                            fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("만료일"),
                            fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                            fieldWithPath("data.tags[]").type(JsonFieldType.ARRAY).description("태그 목록"),
                            fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시"),
                            fieldWithPath("data.startedAt").type(JsonFieldType.STRING).description("시작일시"),
                            fieldWithPath("data.status").type(JsonFieldType.STRING).description("공모전 상태")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/contests/{contestId}", contestId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `존재하지 않는 공모전`() {
            val contestId = 999L
            `when`(contestService.getContestDetail(any(), anyOrNull()))
                .thenThrow(ContestException(ContestErrorCode.CONTEST_NOT_FOUND))

            val documentFilter = document("contest/detail", "CONTEST_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/contests/{contestId}", contestId)
                .then()
                .statusCode(404)
        }
    }
}
