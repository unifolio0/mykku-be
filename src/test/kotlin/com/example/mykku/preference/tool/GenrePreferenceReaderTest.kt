package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GenrePreferenceReaderTest : BaseToolTest() {

    @Mock
    private lateinit var memberGenrePreferenceRepository: MemberGenrePreferenceRepository

    @InjectMocks
    private lateinit var genrePreferenceReader: GenrePreferenceReader

    @Test
    fun `findByMember는 회원의 모든 장르 취향을 반환한다`() {
        // given
        val member = createMockMember()
        val preferences = listOf(
            MemberGenrePreference(id = 1L, member = member, genreType = GenreType.KPOP),
            MemberGenrePreference(id = 2L, member = member, genreType = GenreType.GAME_ESPORTS)
        )

        whenever(memberGenrePreferenceRepository.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = genrePreferenceReader.findByMember(member)

        // then
        assertEquals(2, result.size)
        assertSame(preferences, result)
    }

    @Test
    fun `findByMember는 취향이 없으면 빈 리스트를 반환한다`() {
        // given
        val member = createMockMember()

        whenever(memberGenrePreferenceRepository.findByMember(member))
            .thenReturn(emptyList())

        // when
        val result = genrePreferenceReader.findByMember(member)

        // then
        assertEquals(0, result.size)
    }
}
