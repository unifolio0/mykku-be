package com.example.mykku.docs

import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.board.exception.BoardErrorCode
import com.example.mykku.board.exception.BoardException
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

class BoardDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("게시판 생성")
    inner class CreateBoard {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.BOARD_API,
            summary = "게시판 생성",
            description = "새로운 게시판을 생성합니다.",
            requestBodyFields = listOf(
                fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
            )
        )

        @Test
        fun `성공`() {
            val request = CreateBoardRequest(
                title = "자유게시판",
                logo = "https://example.com/board-logo.png"
            )
            val response = CreateBoardResponse(
                id = 1L,
                title = "자유게시판",
                logo = "https://example.com/board-logo.png"
            )

            `when`(boardService.createBoard(any(), any())).thenReturn(response)

            val documentFilter = document("board/create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 게시판 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시판 제목"),
                            fieldWithPath("data.logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/board")
                .then()
                .statusCode(200)
        }

        @Test
        fun `중복 제목 에러`() {
            val request = CreateBoardRequest(
                title = "자유게시판",
                logo = "https://example.com/board-logo.png"
            )

            `when`(boardService.createBoard(any(), any()))
                .thenThrow(BoardException(BoardErrorCode.BOARD_DUPLICATE_TITLE))

            val documentFilter = document("board/create", "BOARD_DUPLICATE_TITLE")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/board")
                .then()
                .statusCode(400)
        }

        @Test
        fun `제목 길이 초과 에러`() {
            val request = CreateBoardRequest(
                title = "a".repeat(51),
                logo = "https://example.com/board-logo.png"
            )

            `when`(boardService.createBoard(any(), any()))
                .thenThrow(BoardException(BoardErrorCode.BOARD_TITLE_TOO_LONG))

            val documentFilter = document("board/create", "BOARD_TITLE_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/board")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("게시판 수정")
    inner class UpdateBoard {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.BOARD_API,
            summary = "게시판 수정",
            description = "게시판 정보를 수정합니다.",
            pathParameters = listOf(
                parameterWithName("id").description("수정할 게시판 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
            )
        )

        @Test
        fun `성공`() {
            val boardId = 1L
            val request = UpdateBoardRequest(
                title = "수정된 게시판",
                logo = "https://example.com/updated-logo.png"
            )
            val response = UpdateBoardResponse(
                id = boardId,
                title = "수정된 게시판",
                logo = "https://example.com/updated-logo.png"
            )

            `when`(boardService.updateBoard(any(), eq(boardId), any())).thenReturn(response)

            val documentFilter = document("board/update", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시판 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시판 제목"),
                            fieldWithPath("data.logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/board/{id}", boardId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `게시판을 찾을 수 없음`() {
            val boardId = 999L
            val request = UpdateBoardRequest(
                title = "수정된 게시판",
                logo = "https://example.com/updated-logo.png"
            )

            `when`(boardService.updateBoard(any(), eq(boardId), any()))
                .thenThrow(BoardException(BoardErrorCode.BOARD_NOT_FOUND))

            val documentFilter = document("board/update", "BOARD_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/board/{id}", boardId)
                .then()
                .statusCode(404)
        }
    }
}
