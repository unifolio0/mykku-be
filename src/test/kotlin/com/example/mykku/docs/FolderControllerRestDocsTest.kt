package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.scrap.FolderController
import com.example.mykku.scrap.FolderService
import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.FolderResponse
import com.example.mykku.scrap.dto.FoldersResponse
import com.example.mykku.scrap.dto.UpdateFolderRequest
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class FolderControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var folderService: FolderService

    @InjectMocks
    private lateinit var folderController: FolderController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(folderController)
    }

    @Test
    fun `폴더 생성 API 문서화`() {
        // given
        val request = CreateFolderRequest(
            name = "덕질 자료",
            description = "중요한 덕질 자료 모음"
        )
        val response = FolderResponse(
            id = 1L,
            name = "덕질 자료",
            description = "중요한 덕질 자료 모음",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(folderService.createFolder(any(), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/folders")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("폴더가 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "folder-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("폴더 설명")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 폴더 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).optional().description("폴더 설명"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                )
            )
    }

    @Test
    fun `폴더 목록 조회 API 문서화`() {
        // given
        val response = FoldersResponse(
            folders = listOf(
                FolderResponse(
                    id = 1L,
                    name = "덕질 자료",
                    description = "중요한 덕질 자료 모음",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                FolderResponse(
                    id = 2L,
                    name = "이벤트",
                    description = null,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
        )

        `when`(folderService.getFolders(any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/folders")
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("폴더 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "folder-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.folders").type(JsonFieldType.ARRAY).description("폴더 목록"),
                        fieldWithPath("data.folders[].id").type(JsonFieldType.NUMBER).description("폴더 ID"),
                        fieldWithPath("data.folders[].name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("data.folders[].description").type(JsonFieldType.STRING).optional().description("폴더 설명"),
                        fieldWithPath("data.folders[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("data.folders[].updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                )
            )
    }

    @Test
    fun `폴더 수정 API 문서화`() {
        // given
        val folderId = 1L
        val request = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )
        val response = FolderResponse(
            id = folderId,
            name = "수정된 폴더",
            description = "수정된 설명",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(folderService.updateFolder(any(), any(), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/folders/{folderId}", folderId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("폴더가 성공적으로 수정되었습니다."))
            .andDo(
                document(
                    "folder-update",
                    pathParameters(
                        parameterWithName("folderId").description("수정할 폴더 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("폴더 설명")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("폴더 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).optional().description("폴더 설명"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                )
            )
    }

    @Test
    fun `폴더 삭제 API 문서화`() {
        // given
        val folderId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/folders/{folderId}", folderId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("폴더가 성공적으로 삭제되었습니다."))
            .andDo(
                document(
                    "folder-delete",
                    pathParameters(
                        parameterWithName("folderId").description("삭제할 폴더 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `폴더 생성 시 중복된 이름 에러 API 문서화`() {
        // given
        val request = CreateFolderRequest(
            name = "덕질 자료",
            description = "중요한 덕질 자료 모음"
        )

        `when`(folderService.createFolder(any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NAME_DUPLICATE))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/folders")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("이미 존재하는 폴더 이름입니다"))
            .andDo(
                document(
                    "folder-create-error-duplicate",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("폴더 설명")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }

    @Test
    fun `폴더 수정 시 폴더를 찾을 수 없는 경우 에러 API 문서화`() {
        // given
        val folderId = 999L
        val request = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )

        `when`(folderService.updateFolder(any(), any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/folders/{folderId}", folderId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("폴더를 찾을 수 없습니다"))
            .andDo(
                document(
                    "folder-update-error-not-found",
                    pathParameters(
                        parameterWithName("folderId").description("수정할 폴더 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("폴더 설명")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
