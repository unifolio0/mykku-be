package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.board.BoardController
import com.example.mykku.board.BoardService
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

class ErrorResponseRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var boardService: BoardService

    @InjectMocks
    private lateinit var boardController: BoardController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(boardController)
    }

    @Test
    fun `400 Bad Request 에러 응답 문서화`() {
        // given
        val memberId = "member123"
        val request = CreateBoardRequest(
            title = "이미 존재하는 게시판",
            logo = "https://example.com/board-logo.png"
        )

        `when`(boardService.createBoard(request, "member123"))
            .thenThrow(MykkuException(ErrorCode.BOARD_DUPLICATE_TITLE))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/board")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("이미 존재하는 게시판 제목입니다"))
            .andDo(
                document(
                    "error-400",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }

    @Test
    fun `404 Not Found 에러 응답 문서화`() {
        // given
        val memberId = "member123"
        val boardId = 999L
        val request = UpdateBoardRequest(
            title = "수정할 게시판",
            logo = "https://example.com/board-logo.png"
        )

        `when`(boardService.updateBoard(request, boardId, "member123"))
            .thenThrow(MykkuException(ErrorCode.BOARD_NOT_FOUND))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/board/{id}", boardId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("게시판을 찾을 수 없습니다"))
            .andDo(
                document(
                    "error-404",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }

    @Test
    fun `500 Internal Server Error 응답 문서화`() {
        // given
        val memberId = "member123"
        val request = CreateBoardRequest(
            title = "새로운 게시판",
            logo = "https://example.com/board-logo.png"
        )

        `when`(boardService.createBoard(request, "member123"))
            .thenThrow(RuntimeException("예상치 못한 에러"))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/board")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다. 관리자에게 문의해주세요."))
            .andDo(
                document(
                    "error-500",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
