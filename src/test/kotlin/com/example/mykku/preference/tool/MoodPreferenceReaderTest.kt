package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MoodPreferenceReaderTest : BaseToolTest() {

    @Mock
    private lateinit var memberMoodPreferenceRepository: MemberMoodPreferenceRepository

    @InjectMocks
    private lateinit var moodPreferenceReader: MoodPreferenceReader

    @Test
    fun `findByMember는 회원의 모든 분위기 취향을 반환한다`() {
        // given
        val member = createMockMember()
        val preferences = listOf(
            MemberMoodPreference(id = 1L, member = member, moodType = MoodType.COZY),
            MemberMoodPreference(id = 2L, member = member, moodType = MoodType.KITSCH)
        )

        whenever(memberMoodPreferenceRepository.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = moodPreferenceReader.findByMember(member)

        // then
        assertEquals(2, result.size)
        assertSame(preferences, result)
    }

    @Test
    fun `findByMember는 취향이 없으면 빈 리스트를 반환한다`() {
        // given
        val member = createMockMember()

        whenever(memberMoodPreferenceRepository.findByMember(member))
            .thenReturn(emptyList())

        // when
        val result = moodPreferenceReader.findByMember(member)

        // then
        assertEquals(0, result.size)
    }
}
