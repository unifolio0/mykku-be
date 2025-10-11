package com.example.mykku.scrap

import com.example.mykku.BaseControllerTest
import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.UpdateFolderRequest
import com.example.mykku.scrap.repository.FolderRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("FolderController 통합 테스트")
class FolderControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var folderRepository: FolderRepository

    @Test
    @DisplayName("폴더 생성 - 정상 케이스")
    fun `createFolder - 정상적으로 폴더를 생성한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")
        val request = CreateFolderRequest(
            name = "테스트 폴더",
            description = "폴더 설명"
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/folders")
        .then()
            .statusCode(200)
            .body("message", equalTo("폴더가 성공적으로 생성되었습니다."))
            .body("data.id", notNullValue())
            .body("data.name", equalTo(request.name))
            .body("data.description", equalTo(request.description))
    }

    @Test
    @DisplayName("폴더 생성 - 인증되지 않은 사용자")
    fun `createFolder - 인증되지 않은 사용자는 폴더를 생성할 수 없다`() {
        // given
        val request = CreateFolderRequest(
            name = "테스트 폴더",
            description = null
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/folders")
        .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("폴더 목록 조회 - 정상 케이스")
    fun `getFolders - 회원의 폴더 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/folders")
        .then()
            .statusCode(200)
            .body("message", equalTo("폴더 목록을 성공적으로 조회했습니다."))
            .body("data.folders", notNullValue())
    }

    @Test
    @DisplayName("폴더 수정 - 정상 케이스")
    fun `updateFolder - 정상적으로 폴더를 수정한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 먼저 폴더 생성
        val createRequest = CreateFolderRequest(name = "원래 폴더", description = "원래 설명")
        val folderId = RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(createRequest)
        .`when`()
            .post("/api/v1/folders")
        .then()
            .statusCode(200)
            .extract()
            .path<Int>("data.id")
            .toLong()

        val updateRequest = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .`when`()
            .put("/api/v1/folders/$folderId")
        .then()
            .statusCode(200)
            .body("message", equalTo("폴더가 성공적으로 수정되었습니다."))
            .body("data.name", equalTo(updateRequest.name))
            .body("data.description", equalTo(updateRequest.description))
    }

    @Test
    @DisplayName("폴더 삭제 - 정상 케이스")
    fun `deleteFolder - 정상적으로 폴더를 삭제한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 먼저 폴더 생성
        val createRequest = CreateFolderRequest(name = "삭제할 폴더", description = null)
        val folderId = RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(createRequest)
        .`when`()
            .post("/api/v1/folders")
        .then()
            .statusCode(200)
            .extract()
            .path<Int>("data.id")
            .toLong()

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .delete("/api/v1/folders/$folderId")
        .then()
            .statusCode(200)
            .body("message", equalTo("폴더가 성공적으로 삭제되었습니다."))
    }
}
