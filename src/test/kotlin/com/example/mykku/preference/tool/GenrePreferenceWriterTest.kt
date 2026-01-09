package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class GenrePreferenceWriterTest : BaseToolTest() {

    @Mock
    private lateinit var memberGenrePreferenceRepository: MemberGenrePreferenceRepository

    @InjectMocks
    private lateinit var genrePreferenceWriter: GenrePreferenceWriter

    @Test
    fun `replacePreferences는 기존 취향을 삭제하고 새 취향을 저장한다`() {
        // given
        val member = createMockMember()
        val genreTypes = listOf(GenreType.KPOP, GenreType.BAND_ROCK)

        // when
        genrePreferenceWriter.replacePreferences(member, genreTypes)

        // then
        verify(memberGenrePreferenceRepository).deleteByMember(member)
        verify(memberGenrePreferenceRepository).saveAll(any<List<MemberGenrePreference>>())
    }
}
