package com.example.mykku.docs

import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.dto.UpdateGenrePreferenceRequest
import com.example.mykku.preference.dto.UpdateGoodsPreferenceRequest
import com.example.mykku.preference.dto.UpdateMoodPreferenceRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

class PreferenceDocumentTest : BaseDocumentTest() {

    @Test
    fun `장르 취향 저장`() {
        val request = UpdateGenrePreferenceRequest(
            genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK, GenreType.GAME_ESPORTS)
        )

        doNothing().`when`(preferenceService).updateGenrePreferences(any(), any())

        val documentFilter = document("preference/genre-update", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("장르 취향 저장")
                    .description("장르 취향을 저장합니다.")
                    .requestBodyField(
                        fieldWithPath("genreTypes").type(JsonFieldType.ARRAY)
                            .description("장르 취향 목록 (${GenreType.entries.joinToString { it.name }})")
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
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/preferences/genre")
            .then()
            .statusCode(200)
    }

    @Test
    fun `장르 취향 조회`() {
        val genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK)

        `when`(preferenceService.getGenrePreferences(any())).thenReturn(genreTypes)

        val documentFilter = document("preference/genre-get", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("장르 취향 조회")
                    .description("장르 취향을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.genreTypes").type(JsonFieldType.ARRAY)
                            .description("장르 취향 목록 (${GenreType.entries.joinToString { it.name }})")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/preferences/genre")
            .then()
            .statusCode(200)
    }

    @Test
    fun `굿즈 취향 저장`() {
        val request = UpdateGoodsPreferenceRequest(
            goodsTypes = listOf(GoodsType.ITABAG, GoodsType.PHOTOCARD_HOLDER)
        )

        doNothing().`when`(preferenceService).updateGoodsPreferences(any(), any())

        val documentFilter = document("preference/goods-update", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("굿즈 취향 저장")
                    .description("굿즈 취향을 저장합니다.")
                    .requestBodyField(
                        fieldWithPath("goodsTypes").type(JsonFieldType.ARRAY)
                            .description("굿즈 취향 목록 (${GoodsType.entries.joinToString { it.name }})")
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
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/preferences/goods")
            .then()
            .statusCode(200)
    }

    @Test
    fun `굿즈 취향 조회`() {
        val goodsTypes = listOf(GoodsType.PHOTOCARD_HOLDER, GoodsType.UCHIWA)

        `when`(preferenceService.getGoodsPreferences(any())).thenReturn(goodsTypes)

        val documentFilter = document("preference/goods-get", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("굿즈 취향 조회")
                    .description("굿즈 취향을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.goodsTypes").type(JsonFieldType.ARRAY)
                            .description("굿즈 취향 목록 (${GoodsType.entries.joinToString { it.name }})")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/preferences/goods")
            .then()
            .statusCode(200)
    }

    @Test
    fun `분위기 취향 저장`() {
        val request = UpdateMoodPreferenceRequest(
            moodTypes = listOf(MoodType.COZY, MoodType.FRESH)
        )

        doNothing().`when`(preferenceService).updateMoodPreferences(any(), any())

        val documentFilter = document("preference/mood-update", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("분위기 취향 저장")
                    .description("분위기 취향을 저장합니다.")
                    .requestBodyField(
                        fieldWithPath("moodTypes").type(JsonFieldType.ARRAY)
                            .description("분위기 취향 목록 (${MoodType.entries.joinToString { it.name }})")
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
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/preferences/mood")
            .then()
            .statusCode(200)
    }

    @Test
    fun `분위기 취향 조회`() {
        val moodTypes = listOf(MoodType.KITSCH, MoodType.Y2K)

        `when`(preferenceService.getMoodPreferences(any())).thenReturn(moodTypes)

        val documentFilter = document("preference/mood-get", 200)
            .request(
                request()
                    .tag(Tag.PREFERENCE_API)
                    .summary("분위기 취향 조회")
                    .description("분위기 취향을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.moodTypes").type(JsonFieldType.ARRAY)
                            .description("분위기 취향 목록 (${MoodType.entries.joinToString { it.name }})")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/preferences/mood")
            .then()
            .statusCode(200)
    }
}
