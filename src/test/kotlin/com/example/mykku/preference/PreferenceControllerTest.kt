package com.example.mykku.preference

import com.example.mykku.BaseControllerTest
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.dto.UpdateGenrePreferenceRequest
import com.example.mykku.preference.dto.UpdateGoodsPreferenceRequest
import com.example.mykku.preference.dto.UpdateMoodPreferenceRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("PreferenceController 통합 테스트")
class PreferenceControllerTest : BaseControllerTest() {

    @Test
    @DisplayName("장르 취향 저장 - 정상 케이스")
    fun `updateGenrePreferences - 정상적으로 장르 취향을 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")
        val request = UpdateGenrePreferenceRequest(
            genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK)
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/preferences/genre")
        .then()
            .statusCode(200)
            .body("message", equalTo("장르 취향이 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("장르 취향 조회 - 정상 케이스")
    fun `getGenrePreferences - 장르 취향을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 먼저 취향 저장
        val request = UpdateGenrePreferenceRequest(
            genreTypes = listOf(GenreType.KPOP, GenreType.GAME_ESPORTS)
        )
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .post("/api/v1/preferences/genre")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/preferences/genre")
        .then()
            .statusCode(200)
            .body("message", equalTo("장르 취향을 성공적으로 조회했습니다."))
            .body("data.genreTypes", hasSize<Any>(2))
            .body("data.genreTypes", hasItems("KPOP", "GAME_ESPORTS"))
    }

    @Test
    @DisplayName("굿즈 취향 저장 - 정상 케이스")
    fun `updateGoodsPreferences - 정상적으로 굿즈 취향을 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")
        val request = UpdateGoodsPreferenceRequest(
            goodsTypes = listOf(GoodsType.ITABAG, GoodsType.DESK_TERIOR)
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/preferences/goods")
        .then()
            .statusCode(200)
            .body("message", equalTo("굿즈 취향이 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("굿즈 취향 조회 - 정상 케이스")
    fun `getGoodsPreferences - 굿즈 취향을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 먼저 취향 저장
        val request = UpdateGoodsPreferenceRequest(
            goodsTypes = listOf(GoodsType.PHOTOCARD_HOLDER, GoodsType.NAME_BOARD)
        )
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .post("/api/v1/preferences/goods")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/preferences/goods")
        .then()
            .statusCode(200)
            .body("message", equalTo("굿즈 취향을 성공적으로 조회했습니다."))
            .body("data.goodsTypes", hasSize<Any>(2))
            .body("data.goodsTypes", hasItems("PHOTOCARD_HOLDER", "NAME_BOARD"))
    }

    @Test
    @DisplayName("분위기 취향 저장 - 정상 케이스")
    fun `updateMoodPreferences - 정상적으로 분위기 취향을 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")
        val request = UpdateMoodPreferenceRequest(
            moodTypes = listOf(MoodType.COZY, MoodType.KITSCH)
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/preferences/mood")
        .then()
            .statusCode(200)
            .body("message", equalTo("분위기 취향이 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("분위기 취향 조회 - 정상 케이스")
    fun `getMoodPreferences - 분위기 취향을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 먼저 취향 저장
        val request = UpdateMoodPreferenceRequest(
            moodTypes = listOf(MoodType.FRESH, MoodType.Y2K)
        )
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .post("/api/v1/preferences/mood")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/preferences/mood")
        .then()
            .statusCode(200)
            .body("message", equalTo("분위기 취향을 성공적으로 조회했습니다."))
            .body("data.moodTypes", hasSize<Any>(2))
            .body("data.moodTypes", hasItems("FRESH", "Y2K"))
    }

    @Test
    @DisplayName("취향 저장 - 인증되지 않은 사용자")
    fun `updatePreferences - 인증되지 않은 사용자는 취향을 저장할 수 없다`() {
        // given
        val request = UpdateGenrePreferenceRequest(
            genreTypes = listOf(GenreType.KPOP)
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/preferences/genre")
        .then()
            .statusCode(401)
    }
}
