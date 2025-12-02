package com.example.mykku.docs

import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.FolderResponse
import com.example.mykku.scrap.dto.FoldersResponse
import com.example.mykku.scrap.dto.UpdateFolderRequest
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class FolderDocumentTest : BaseDocumentTest() {

    @Test
    fun `폴더 생성`() {
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

        val documentFilter = document("folder/create", 200)
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 생성")
                    .description("새로운 스크랩 폴더를 생성합니다.")
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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
    fun `폴더 생성 - 중복 이름 에러`() {
        val request = CreateFolderRequest(
            name = "덕질 자료",
            description = "중요한 덕질 자료 모음"
        )

        `when`(folderService.createFolder(any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NAME_DUPLICATE))

        val documentFilter = document("folder/create", "FOLDER_NAME_DUPLICATE")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 생성 - 중복 이름 에러")
                    .description("이미 존재하는 폴더 이름으로 생성 시도 시 발생하는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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
    fun `폴더 생성 - 이름 길이 초과 에러`() {
        val request = CreateFolderRequest(
            name = "a".repeat(51),
            description = "중요한 덕질 자료 모음"
        )

        `when`(folderService.createFolder(any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NAME_TOO_LONG))

        val documentFilter = document("folder/create", "FOLDER_NAME_TOO_LONG")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 생성 - 이름 길이 초과 에러")
                    .description("폴더 이름이 최대 길이(50자)를 초과했을 때 발생하는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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
    fun `폴더 목록 조회`() {
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

        val documentFilter = document("folder/list", 200)
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 목록 조회")
                    .description("사용자의 스크랩 폴더 목록을 조회합니다.")
            )
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

    @Test
    fun `폴더 수정`() {
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

        val documentFilter = document("folder/update", 200)
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 수정")
                    .description("폴더 정보를 수정합니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("수정할 폴더 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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
    fun `폴더 수정 - 폴더를 찾을 수 없음`() {
        val folderId = 999L
        val request = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )

        `when`(folderService.updateFolder(any(), any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND))

        val documentFilter = document("folder/update", "FOLDER_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 수정 - 폴더를 찾을 수 없음")
                    .description("존재하지 않는 폴더를 수정하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("수정할 폴더 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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
    fun `폴더 수정 - 권한 없음`() {
        val folderId = 1L
        val request = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )

        `when`(folderService.updateFolder(any(), any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_UNAUTHORIZED))

        val documentFilter = document("folder/update", "FOLDER_UNAUTHORIZED")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 수정 - 권한 없음")
                    .description("해당 폴더에 접근할 권한이 없을 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("수정할 폴더 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("폴더 설명").optional()
                    )
            )
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

    @Test
    fun `폴더 삭제`() {
        val folderId = 1L

        val documentFilter = document("folder/delete", 200)
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 삭제")
                    .description("폴더를 삭제합니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("삭제할 폴더 ID")
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
            .delete("/api/v1/folders/{folderId}", folderId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `폴더 삭제 - 폴더를 찾을 수 없음`() {
        val folderId = 999L

        `when`(folderService.deleteFolder(any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND))

        val documentFilter = document("folder/delete", "FOLDER_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 삭제 - 폴더를 찾을 수 없음")
                    .description("존재하지 않는 폴더를 삭제하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("삭제할 폴더 ID")
                    )
            )
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
    fun `폴더 삭제 - 권한 없음`() {
        val folderId = 1L

        `when`(folderService.deleteFolder(any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.FOLDER_UNAUTHORIZED))

        val documentFilter = document("folder/delete", "FOLDER_UNAUTHORIZED")
            .request(
                request()
                    .tag(Tag.FOLDER_API)
                    .summary("폴더 삭제 - 권한 없음")
                    .description("해당 폴더를 삭제할 권한이 없을 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("folderId").description("삭제할 폴더 ID")
                    )
            )
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
