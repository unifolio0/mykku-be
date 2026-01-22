package com.example.mykku.scrap.adapter.input.web

import com.example.mykku.BaseDocumentTest
import com.example.mykku.docs.ApiRequestConfig
import com.example.mykku.docs.RestDocumentationResponse
import com.example.mykku.docs.Tag
import com.example.mykku.scrap.adapter.input.web.dto.CreateFolderWebRequest
import com.example.mykku.scrap.adapter.input.web.dto.UpdateFolderWebRequest
import com.example.mykku.scrap.application.dto.FolderResult
import com.example.mykku.scrap.application.dto.FoldersResult
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class FolderDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("폴더 생성")
    inner class CreateFolder {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FOLDER_API,
            summary = "폴더 생성",
            description = "새로운 스크랩 폴더를 생성합니다.",
            requestBodyFields = listOf(
                fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
            )
        )

        @Test
        fun `성공`() {
            val request = CreateFolderWebRequest(
                name = "덕질 자료",
                description = "중요한 덕질 자료 모음"
            )
            val response = FolderResult(
                id = 1L,
                name = "덕질 자료",
                description = "중요한 덕질 자료 모음",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            `when`(folderUseCase.createFolder(any())).thenReturn(response)

            val documentFilter = document("folder/create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 폴더 ID"),
                            fieldWithPath("data.name").type(JsonFieldType.STRING).description("폴더 이름"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("폴더 설명").optional(),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                            fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/folders")
                .then()
                .statusCode(200)
        }

        @Test
        fun `중복 이름 에러`() {
            val request = CreateFolderWebRequest(
                name = "덕질 자료",
                description = "중요한 덕질 자료 모음"
            )

            `when`(folderUseCase.createFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NAME_DUPLICATE))

            val documentFilter = document("folder/create", "FOLDER_NAME_DUPLICATE")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/folders")
                .then()
                .statusCode(400)
        }

        @Test
        fun `이름 길이 초과 에러`() {
            val request = CreateFolderWebRequest(
                name = "a".repeat(51),
                description = "중요한 덕질 자료 모음"
            )

            `when`(folderUseCase.createFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NAME_TOO_LONG))

            val documentFilter = document("folder/create", "FOLDER_NAME_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/folders")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("폴더 목록 조회")
    inner class GetFolderList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FOLDER_API,
            summary = "폴더 목록 조회",
            description = "사용자의 스크랩 폴더 목록을 조회합니다."
        )

        @Test
        fun `성공`() {
            val response = FoldersResult(
                folders = listOf(
                    FolderResult(
                        id = 1L,
                        name = "덕질 자료",
                        description = "중요한 덕질 자료 모음",
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    ),
                    FolderResult(
                        id = 2L,
                        name = "이벤트",
                        description = null,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
            )

            `when`(folderUseCase.getFolders(any())).thenReturn(response)

            val documentFilter = document("folder/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.folders[]").type(JsonFieldType.ARRAY).description("폴더 목록"),
                            fieldWithPath("data.folders[].id").type(JsonFieldType.NUMBER).description("폴더 ID"),
                            fieldWithPath("data.folders[].name").type(JsonFieldType.STRING).description("폴더 이름"),
                            fieldWithPath("data.folders[].description").type(JsonFieldType.STRING).description("폴더 설명")
                                .optional(),
                            fieldWithPath("data.folders[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                            fieldWithPath("data.folders[].updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/folders")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("폴더 수정")
    inner class UpdateFolder {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FOLDER_API,
            summary = "폴더 수정",
            description = "폴더 정보를 수정합니다.",
            pathParameters = listOf(
                parameterWithName("folderId").description("수정할 폴더 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
            )
        )

        @Test
        fun `성공`() {
            val folderId = 1L
            val request = UpdateFolderWebRequest(
                name = "수정된 폴더",
                description = "수정된 설명"
            )
            val response = FolderResult(
                id = folderId,
                name = "수정된 폴더",
                description = "수정된 설명",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            `when`(folderUseCase.updateFolder(any())).thenReturn(response)

            val documentFilter = document("folder/update", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("폴더 ID"),
                            fieldWithPath("data.name").type(JsonFieldType.STRING).description("폴더 이름"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("폴더 설명").optional(),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                            fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `폴더를 찾을 수 없음`() {
            val folderId = 999L
            val request = UpdateFolderWebRequest(
                name = "수정된 폴더",
                description = "수정된 설명"
            )

            `when`(folderUseCase.updateFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND))

            val documentFilter = document("folder/update", "FOLDER_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(404)
        }

        @Test
        fun `권한 없음`() {
            val folderId = 1L
            val request = UpdateFolderWebRequest(
                name = "수정된 폴더",
                description = "수정된 설명"
            )

            `when`(folderUseCase.updateFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_UNAUTHORIZED))

            val documentFilter = document("folder/update", "FOLDER_UNAUTHORIZED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(403)
        }
    }

    @Nested
    @DisplayName("폴더 삭제")
    inner class DeleteFolder {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FOLDER_API,
            summary = "폴더 삭제",
            description = "폴더를 삭제합니다.",
            pathParameters = listOf(
                parameterWithName("folderId").description("삭제할 폴더 ID")
            )
        )

        @Test
        fun `성공`() {
            val folderId = 1L

            val documentFilter = document("folder/delete", 200)
                .request(request().applyConfig(apiConfig))
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
                .delete("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `폴더를 찾을 수 없음`() {
            val folderId = 999L

            `when`(folderUseCase.deleteFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND))

            val documentFilter = document("folder/delete", "FOLDER_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(404)
        }

        @Test
        fun `권한 없음`() {
            val folderId = 1L

            `when`(folderUseCase.deleteFolder(any()))
                .thenThrow(ScrapException(ScrapErrorCode.FOLDER_UNAUTHORIZED))

            val documentFilter = document("folder/delete", "FOLDER_UNAUTHORIZED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/folders/{folderId}", folderId)
                .then()
                .statusCode(403)
        }
    }
}
