package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.preference.PreferenceController
import com.example.mykku.preference.PreferenceService
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.dto.*
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

class PreferenceControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var preferenceService: PreferenceService

    @InjectMocks
    private lateinit var preferenceController: PreferenceController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(preferenceController)
    }

    @Test
    fun `장르 취향 저장 API 문서화`() {
        // given
        val request = UpdateGenrePreferenceRequest(
            genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK, GenreType.GAME_ESPORTS)
        )

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/preferences/genre")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("장르 취향이 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "preference-genre-update",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("genreTypes").type(JsonFieldType.ARRAY)
                            .description("장르 취향 목록 (${GenreType.entries.joinToString { it.name }})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `장르 취향 조회 API 문서화`() {
        // given
        val response = GenrePreferenceResponse(
            genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK)
        )

        `when`(preferenceService.getGenrePreferences(any())).thenReturn(response.genreTypes)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/preferences/genre")
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("장르 취향을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "preference-genre-get",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.genreTypes").type(JsonFieldType.ARRAY)
                            .description("장르 취향 목록 (${GenreType.entries.joinToString { it.name }}")
                    )
                )
            )
    }

    @Test
    fun `굿즈 취향 저장 API 문서화`() {
        // given
        val request = UpdateGoodsPreferenceRequest(
            goodsTypes = listOf(GoodsType.ITABAG, GoodsType.PHOTOCARD_HOLDER)
        )

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/preferences/goods")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("굿즈 취향이 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "preference-goods-update",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("goodsTypes").type(JsonFieldType.ARRAY)
                            .description("굿즈 취향 목록 (${GoodsType.entries.joinToString { it.name }})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `굿즈 취향 조회 API 문서화`() {
        // given
        val response = GoodsPreferenceResponse(
            goodsTypes = listOf(GoodsType.PHOTOCARD_HOLDER, GoodsType.UCHIWA)
        )

        `when`(preferenceService.getGoodsPreferences(any())).thenReturn(response.goodsTypes)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/preferences/goods")
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("굿즈 취향을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "preference-goods-get",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.goodsTypes").type(JsonFieldType.ARRAY)
                            .description("굿즈 취향 목록 (${GoodsType.entries.joinToString { it.name }})")
                    )
                )
            )
    }

    @Test
    fun `분위기 취향 저장 API 문서화`() {
        // given
        val request = UpdateMoodPreferenceRequest(
            moodTypes = listOf(MoodType.COZY, MoodType.FRESH)
        )

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/preferences/mood")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("분위기 취향이 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "preference-mood-update",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("moodTypes").type(JsonFieldType.ARRAY)
                            .description("분위기 취향 목록 (${MoodType.entries.joinToString { it.name }})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `분위기 취향 조회 API 문서화`() {
        // given
        val response = MoodPreferenceResponse(
            moodTypes = listOf(MoodType.KITSCH, MoodType.Y2K)
        )

        `when`(preferenceService.getMoodPreferences(any())).thenReturn(response.moodTypes)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/preferences/mood")
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("분위기 취향을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "preference-mood-get",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.moodTypes").type(JsonFieldType.ARRAY)
                            .description("분위기 취향 목록 (${MoodType.entries.joinToString { it.name }})")
                    )
                )
            )
    }
}
