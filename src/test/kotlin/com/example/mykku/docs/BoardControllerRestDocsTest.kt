package com.example.mykku.docs

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.board.BoardController
import com.example.mykku.board.BoardService
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.GlobalExceptionHandler
import com.example.mykku.exception.MykkuException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class BoardControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var boardService: BoardService

    @InjectMocks
    private lateinit var boardController: BoardController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController)
            .setControllerAdvice(GlobalExceptionHandler())
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .setCustomArgumentResolvers(TestMemberArgumentResolver())
            .apply<StandaloneMockMvcBuilder>(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        modifyUris()
                            .scheme("https")
                            .host("api.mykku.com")
                            .removePort(),
                        prettyPrint()
                    )
                    .withResponseDefaults(
                        modifyHeaders()
                            .remove("X-Content-Type-Options")
                            .remove("X-XSS-Protection")
                            .remove("Cache-Control")
                            .remove("Pragma")
                            .remove("Expires")
                            .remove("X-Frame-Options"),
                        prettyPrint()
                    )
            )
            .build()
    }

    @Test
    fun `게시판 생성 API 문서화`() {
        // given
        val memberId = "member123"
        val request = CreateBoardRequest(
            title = "자유게시판",
            logo = "https://example.com/board-logo.png"
        )
        val response = CreateBoardResponse(
            id = 1L,
            title = "자유게시판",
            logo = "https://example.com/board-logo.png"
        )

        `when`(boardService.createBoard(request, "member123")).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/board")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("게시판이 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "board-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 게시판 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("data.logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    )
                )
            )
    }

    @Test
    fun `게시판 수정 API 문서화`() {
        // given
        val memberId = "member123"
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

        `when`(boardService.updateBoard(request, boardId, "member123")).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/board/{id}", boardId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("게시판이 성공적으로 수정되었습니다."))
            .andDo(
                document(
                    "board-update",
                    pathParameters(
                        parameterWithName("id").description("수정할 게시판 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시판 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("data.logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    )
                )
            )
    }

    @Test
    fun `게시판 생성 시 중복된 제목 에러 API 문서화`() {
        // given
        val memberId = "member123"
        val request = CreateBoardRequest(
            title = "자유게시판",
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
                    "board-create-error-duplicate",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }

    @Test
    fun `게시판 수정 시 게시판을 찾을 수 없는 경우 에러 API 문서화`() {
        // given
        val memberId = "member123"
        val boardId = 999L
        val request = UpdateBoardRequest(
            title = "수정된 게시판",
            logo = "https://example.com/updated-logo.png"
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
                    "board-update-error-not-found",
                    pathParameters(
                        parameterWithName("id").description("수정할 게시판 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
