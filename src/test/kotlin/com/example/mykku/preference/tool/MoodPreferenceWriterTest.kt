package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class MoodPreferenceWriterTest : BaseToolTest() {

    @Mock
    private lateinit var memberMoodPreferenceRepository: MemberMoodPreferenceRepository

    @InjectMocks
    private lateinit var moodPreferenceWriter: MoodPreferenceWriter

    @Test
    fun `replacePreferences는 기존 취향을 삭제하고 새 취향을 저장한다`() {
        // given
        val member = createMockMember()
        val moodTypes = listOf(MoodType.FRESH, MoodType.Y2K)

        // when
        moodPreferenceWriter.replacePreferences(member, moodTypes)

        // then
        verify(memberMoodPreferenceRepository).deleteByMember(member)
        verify(memberMoodPreferenceRepository).saveAll(org.mockito.kotlin.any<List<MemberMoodPreference>>())
    }
}
