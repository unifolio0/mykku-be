package com.example.mykku.preference

import com.example.mykku.BaseServiceTest
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.tool.GenrePreferenceReader
import com.example.mykku.preference.tool.GenrePreferenceWriter
import com.example.mykku.preference.tool.GoodsPreferenceReader
import com.example.mykku.preference.tool.GoodsPreferenceWriter
import com.example.mykku.preference.tool.MoodPreferenceReader
import com.example.mykku.preference.tool.MoodPreferenceWriter
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PreferenceServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var genrePreferenceReader: GenrePreferenceReader

    @Mock
    private lateinit var genrePreferenceWriter: GenrePreferenceWriter

    @Mock
    private lateinit var goodsPreferenceReader: GoodsPreferenceReader

    @Mock
    private lateinit var goodsPreferenceWriter: GoodsPreferenceWriter

    @Mock
    private lateinit var moodPreferenceReader: MoodPreferenceReader

    @Mock
    private lateinit var moodPreferenceWriter: MoodPreferenceWriter

    @InjectMocks
    private lateinit var preferenceService: PreferenceService

    @Test
    fun `updateGenrePreferences는 장르 취향을 업데이트한다`() {
        // given
        val member = createTestMember()
        val genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK)

        // when
        preferenceService.updateGenrePreferences(member, genreTypes)

        // then
        verify(genrePreferenceWriter).replacePreferences(member, genreTypes)
    }

    @Test
    fun `getGenrePreferences는 장르 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberGenrePreference(id = 1L, member = member, genreType = GenreType.KPOP),
            MemberGenrePreference(id = 2L, member = member, genreType = GenreType.GAME_ESPORTS)
        )

        whenever(genrePreferenceReader.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getGenrePreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(GenreType.KPOP, result[0])
        assertEquals(GenreType.GAME_ESPORTS, result[1])
        verify(genrePreferenceReader).findByMember(member)
    }

    @Test
    fun `updateGoodsPreferences는 굿즈 취향을 업데이트한다`() {
        // given
        val member = createTestMember()
        val goodsTypes = listOf(GoodsType.ITABAG, GoodsType.DESK_TERIOR)

        // when
        preferenceService.updateGoodsPreferences(member, goodsTypes)

        // then
        verify(goodsPreferenceWriter).replacePreferences(member, goodsTypes)
    }

    @Test
    fun `getGoodsPreferences는 굿즈 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberGoodsPreference(id = 1L, member = member, goodsType = GoodsType.PHOTOCARD_HOLDER),
            MemberGoodsPreference(id = 2L, member = member, goodsType = GoodsType.UCHIWA)
        )

        whenever(goodsPreferenceReader.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getGoodsPreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(GoodsType.PHOTOCARD_HOLDER, result[0])
        assertEquals(GoodsType.UCHIWA, result[1])
        verify(goodsPreferenceReader).findByMember(member)
    }

    @Test
    fun `updateMoodPreferences는 분위기 취향을 업데이트한다`() {
        // given
        val member = createTestMember()
        val moodTypes = listOf(MoodType.COZY, MoodType.FRESH)

        // when
        preferenceService.updateMoodPreferences(member, moodTypes)

        // then
        verify(moodPreferenceWriter).replacePreferences(member, moodTypes)
    }

    @Test
    fun `getMoodPreferences는 분위기 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberMoodPreference(id = 1L, member = member, moodType = MoodType.KITSCH),
            MemberMoodPreference(id = 2L, member = member, moodType = MoodType.Y2K)
        )

        whenever(moodPreferenceReader.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getMoodPreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(MoodType.KITSCH, result[0])
        assertEquals(MoodType.Y2K, result[1])
        verify(moodPreferenceReader).findByMember(member)
    }
}
