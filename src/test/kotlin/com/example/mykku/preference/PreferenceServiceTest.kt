package com.example.mykku.preference

import com.example.mykku.BaseServiceTest
import com.example.mykku.preference.application.port.out.GenrePreferenceQueryPort
import com.example.mykku.preference.application.port.out.GenrePreferenceRepositoryPort
import com.example.mykku.preference.application.port.out.GoodsPreferenceQueryPort
import com.example.mykku.preference.application.port.out.GoodsPreferenceRepositoryPort
import com.example.mykku.preference.application.port.out.MoodPreferenceQueryPort
import com.example.mykku.preference.application.port.out.MoodPreferenceRepositoryPort
import com.example.mykku.preference.domain.*
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class PreferenceServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var genrePreferenceQueryPort: GenrePreferenceQueryPort

    @Mock
    private lateinit var genrePreferenceRepositoryPort: GenrePreferenceRepositoryPort

    @Mock
    private lateinit var goodsPreferenceQueryPort: GoodsPreferenceQueryPort

    @Mock
    private lateinit var goodsPreferenceRepositoryPort: GoodsPreferenceRepositoryPort

    @Mock
    private lateinit var moodPreferenceQueryPort: MoodPreferenceQueryPort

    @Mock
    private lateinit var moodPreferenceRepositoryPort: MoodPreferenceRepositoryPort

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
        verify(genrePreferenceRepositoryPort).replacePreferences(member, genreTypes)
    }

    @Test
    fun `getGenrePreferences는 장르 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberGenrePreference(id = 1L, member = member, genreType = GenreType.KPOP),
            MemberGenrePreference(id = 2L, member = member, genreType = GenreType.GAME_ESPORTS)
        )

        whenever(genrePreferenceQueryPort.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getGenrePreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(GenreType.KPOP, result[0])
        assertEquals(GenreType.GAME_ESPORTS, result[1])
        verify(genrePreferenceQueryPort).findByMember(member)
    }

    @Test
    fun `updateGoodsPreferences는 굿즈 취향을 업데이트한다`() {
        // given
        val member = createTestMember()
        val goodsTypes = listOf(GoodsType.ITABAG, GoodsType.DESK_TERIOR)

        // when
        preferenceService.updateGoodsPreferences(member, goodsTypes)

        // then
        verify(goodsPreferenceRepositoryPort).replacePreferences(member, goodsTypes)
    }

    @Test
    fun `getGoodsPreferences는 굿즈 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberGoodsPreference(id = 1L, member = member, goodsType = GoodsType.PHOTOCARD_HOLDER),
            MemberGoodsPreference(id = 2L, member = member, goodsType = GoodsType.UCHIWA)
        )

        whenever(goodsPreferenceQueryPort.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getGoodsPreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(GoodsType.PHOTOCARD_HOLDER, result[0])
        assertEquals(GoodsType.UCHIWA, result[1])
        verify(goodsPreferenceQueryPort).findByMember(member)
    }

    @Test
    fun `updateMoodPreferences는 분위기 취향을 업데이트한다`() {
        // given
        val member = createTestMember()
        val moodTypes = listOf(MoodType.COZY, MoodType.FRESH)

        // when
        preferenceService.updateMoodPreferences(member, moodTypes)

        // then
        verify(moodPreferenceRepositoryPort).replacePreferences(member, moodTypes)
    }

    @Test
    fun `getMoodPreferences는 분위기 취향 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val preferences = listOf(
            MemberMoodPreference(id = 1L, member = member, moodType = MoodType.KITSCH),
            MemberMoodPreference(id = 2L, member = member, moodType = MoodType.Y2K)
        )

        whenever(moodPreferenceQueryPort.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = preferenceService.getMoodPreferences(member)

        // then
        assertEquals(2, result.size)
        assertEquals(MoodType.KITSCH, result[0])
        assertEquals(MoodType.Y2K, result[1])
        verify(moodPreferenceQueryPort).findByMember(member)
    }
}
