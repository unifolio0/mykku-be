package com.example.mykku.docs

import com.example.mykku.contest.dto.*
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class ContestWinnerDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("수상자 선정")
    inner class SetWinners {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.ADMIN_CONTEST_API,
            summary = "수상자 선정",
            description = "콘테스트 수상자를 선정합니다. (관리자 전용)",
            pathParameters = listOf(
                parameterWithName("contestId").description("콘테스트 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("winners[]").type(JsonFieldType.ARRAY).description("수상자 목록"),
                fieldWithPath("winners[].participationId").type(JsonFieldType.NUMBER).description("참여 ID"),
                fieldWithPath("winners[].winnerRank").type(JsonFieldType.NUMBER).description("순위 (1, 2, 3)"),
                fieldWithPath("winners[].description").type(JsonFieldType.STRING).description("수상 설명").optional()
            )
        )

        @Test
        fun `성공`() {
            val contestId = 1L
            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1, description = "1등 설명"),
                    SetContestWinnersRequest.WinnerSelection(participationId = 2L, winnerRank = 2, description = "2등 설명"),
                    SetContestWinnersRequest.WinnerSelection(participationId = 3L, winnerRank = 3, description = "3등 설명")
                )
            )

            val response = SetContestWinnersResponse(
                contestId = contestId,
                contestTitle = "테스트 콘테스트",
                winners = listOf(
                    SetContestWinnersResponse.WinnerInfo(winnerId = 1L, winnerRank = 1, feedId = 10L, feedTitle = "1등 피드", authorNickname = "user1"),
                    SetContestWinnersResponse.WinnerInfo(winnerId = 2L, winnerRank = 2, feedId = 20L, feedTitle = "2등 피드", authorNickname = "user2"),
                    SetContestWinnersResponse.WinnerInfo(winnerId = 3L, winnerRank = 3, feedId = 30L, feedTitle = "3등 피드", authorNickname = "user3")
                )
            )

            `when`(contestWinnerService.setWinners(eq(contestId), any())).thenReturn(response)

            val documentFilter = document("admin-contest/set-winners", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.contestId").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                            fieldWithPath("data.contestTitle").type(JsonFieldType.STRING).description("콘테스트 제목"),
                            fieldWithPath("data.winners[]").type(JsonFieldType.ARRAY).description("수상자 목록"),
                            fieldWithPath("data.winners[].winnerId").type(JsonFieldType.NUMBER).description("수상자 ID"),
                            fieldWithPath("data.winners[].winnerRank").type(JsonFieldType.NUMBER).description("순위"),
                            fieldWithPath("data.winners[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.winners[].feedTitle").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.winners[].authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/admin/contests/{contestId}/winners", contestId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `콘테스트 미종료`() {
            val contestId = 1L
            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1)
                )
            )

            `when`(contestWinnerService.setWinners(eq(contestId), any()))
                .thenThrow(ContestException(ContestErrorCode.CONTEST_NOT_EXPIRED))

            val documentFilter = document("admin-contest/set-winners", "CONTEST_NOT_EXPIRED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/admin/contests/{contestId}/winners", contestId)
                .then()
                .statusCode(400)
        }

        @Test
        fun `중복 순위`() {
            val contestId = 1L
            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1),
                    SetContestWinnersRequest.WinnerSelection(participationId = 2L, winnerRank = 1)
                )
            )

            `when`(contestWinnerService.setWinners(eq(contestId), any()))
                .thenThrow(ContestException(ContestErrorCode.DUPLICATE_WINNER_RANK))

            val documentFilter = document("admin-contest/set-winners", "DUPLICATE_WINNER_RANK")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/admin/contests/{contestId}/winners", contestId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("수상작 목록 조회")
    inner class GetContestsWithWinners {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_WINNER_API,
            summary = "수상작 목록 조회",
            description = "모든 콘테스트의 수상작 미리보기를 조회합니다."
        )

        @Test
        fun `성공`() {
            val response = ContestWinnersListResponse(
                contests = listOf(
                    ContestWinnersListResponse.ContestWinnerPreview(
                        contestId = 1L,
                        contestTitle = "첫 번째 콘테스트",
                        winners = listOf(
                            ContestWinnersListResponse.WinnerThumbnail(winnerId = 1L, winnerRank = 1, feedImageUrl = "https://example.com/image1.jpg"),
                            ContestWinnersListResponse.WinnerThumbnail(winnerId = 2L, winnerRank = 2, feedImageUrl = "https://example.com/image2.jpg"),
                            ContestWinnersListResponse.WinnerThumbnail(winnerId = 3L, winnerRank = 3, feedImageUrl = null)
                        )
                    ),
                    ContestWinnersListResponse.ContestWinnerPreview(
                        contestId = 2L,
                        contestTitle = "두 번째 콘테스트",
                        winners = listOf(
                            ContestWinnersListResponse.WinnerThumbnail(winnerId = 4L, winnerRank = 1, feedImageUrl = "https://example.com/image4.jpg")
                        )
                    )
                )
            )

            `when`(contestWinnerService.getContestsWithWinners()).thenReturn(response)

            val documentFilter = document("contest-winner/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.contests[]").type(JsonFieldType.ARRAY).description("콘테스트 목록"),
                            fieldWithPath("data.contests[].contestId").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                            fieldWithPath("data.contests[].contestTitle").type(JsonFieldType.STRING).description("콘테스트 제목"),
                            fieldWithPath("data.contests[].winners[]").type(JsonFieldType.ARRAY).description("수상자 목록"),
                            fieldWithPath("data.contests[].winners[].winnerId").type(JsonFieldType.NUMBER).description("수상자 ID"),
                            fieldWithPath("data.contests[].winners[].winnerRank").type(JsonFieldType.NUMBER).description("순위"),
                            fieldWithPath("data.contests[].winners[].feedImageUrl").type(JsonFieldType.STRING).description("피드 이미지 URL").optional()
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/contests/winners")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("수상작 상세 조회")
    inner class GetContestWinnerDetail {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_WINNER_API,
            summary = "수상작 상세 조회",
            description = "특정 콘테스트의 수상작 상세 정보를 조회합니다.",
            pathParameters = listOf(
                parameterWithName("contestId").description("콘테스트 ID")
            )
        )

        @Test
        fun `성공`() {
            val contestId = 1L
            val response = ContestWinnerDetailResponse(
                contestId = contestId,
                contestTitle = "테스트 콘테스트",
                winners = listOf(
                    ContestWinnerDetailResponse.WinnerDetail(
                        winnerId = 1L,
                        winnerRank = 1,
                        feedId = 10L,
                        feedTitle = "1등 작품",
                        feedImageUrl = "https://example.com/image1.jpg",
                        authorNickname = "user1",
                        authorProfileImage = "https://example.com/profile1.jpg",
                        description = "1등 수상 설명",
                        acceptanceSpeech = "감사합니다!"
                    ),
                    ContestWinnerDetailResponse.WinnerDetail(
                        winnerId = 2L,
                        winnerRank = 2,
                        feedId = 20L,
                        feedTitle = "2등 작품",
                        feedImageUrl = "https://example.com/image2.jpg",
                        authorNickname = "user2",
                        authorProfileImage = null,
                        description = "2등 수상 설명",
                        acceptanceSpeech = ""
                    )
                )
            )

            `when`(contestWinnerService.getContestWinnerDetail(contestId)).thenReturn(response)

            val documentFilter = document("contest-winner/detail", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.contestId").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                            fieldWithPath("data.contestTitle").type(JsonFieldType.STRING).description("콘테스트 제목"),
                            fieldWithPath("data.winners[]").type(JsonFieldType.ARRAY).description("수상자 목록"),
                            fieldWithPath("data.winners[].winnerId").type(JsonFieldType.NUMBER).description("수상자 ID"),
                            fieldWithPath("data.winners[].winnerRank").type(JsonFieldType.NUMBER).description("순위"),
                            fieldWithPath("data.winners[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.winners[].feedTitle").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.winners[].feedImageUrl").type(JsonFieldType.STRING).description("피드 이미지 URL").optional(),
                            fieldWithPath("data.winners[].authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                            fieldWithPath("data.winners[].authorProfileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
                            fieldWithPath("data.winners[].description").type(JsonFieldType.STRING).description("수상 설명"),
                            fieldWithPath("data.winners[].acceptanceSpeech").type(JsonFieldType.STRING).description("수상 소감")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/contests/{contestId}/winners", contestId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `콘테스트 없음`() {
            val contestId = 999L

            `when`(contestWinnerService.getContestWinnerDetail(contestId))
                .thenThrow(ContestException(ContestErrorCode.CONTEST_NOT_FOUND))

            val documentFilter = document("contest-winner/detail", "CONTEST_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/contests/{contestId}/winners", contestId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("수상 소감 등록")
    inner class UpdateAcceptanceSpeech {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.CONTEST_WINNER_API,
            summary = "수상 소감 등록",
            description = "수상자 본인이 수상 소감을 등록합니다.",
            pathParameters = listOf(
                parameterWithName("winnerId").description("수상자 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("acceptanceSpeech").type(JsonFieldType.STRING).description("수상 소감 (최대 1000자)")
            )
        )

        @Test
        fun `성공`() {
            val winnerId = 1L
            val request = UpdateAcceptanceSpeechRequest(acceptanceSpeech = "정말 감사합니다. 이 영광을 가족에게 돌립니다.")
            val response = UpdateAcceptanceSpeechResponse(winnerId = winnerId, acceptanceSpeech = request.acceptanceSpeech)

            `when`(contestWinnerService.updateAcceptanceSpeech(eq(winnerId), any(), any())).thenReturn(response)

            val documentFilter = document("contest-winner/update-acceptance-speech", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.winnerId").type(JsonFieldType.NUMBER).description("수상자 ID"),
                            fieldWithPath("data.acceptanceSpeech").type(JsonFieldType.STRING).description("수상 소감")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/contests/winners/{winnerId}/acceptance-speech", winnerId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `수상자 아님`() {
            val winnerId = 1L
            val request = UpdateAcceptanceSpeechRequest(acceptanceSpeech = "소감입니다.")

            `when`(contestWinnerService.updateAcceptanceSpeech(eq(winnerId), any(), any()))
                .thenThrow(ContestException(ContestErrorCode.NOT_WINNER_OWNER))

            val documentFilter = document("contest-winner/update-acceptance-speech", "NOT_WINNER_OWNER")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/contests/winners/{winnerId}/acceptance-speech", winnerId)
                .then()
                .statusCode(403)
        }
    }
}
