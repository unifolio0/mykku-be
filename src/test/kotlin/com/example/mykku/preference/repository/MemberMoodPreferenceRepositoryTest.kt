package com.example.mykku.preference.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DisplayName("MemberMoodPreferenceRepository 테스트")
class MemberMoodPreferenceRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberMoodPreferenceRepository: MemberMoodPreferenceRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("분위기 취향을 저장하고 조회한다")
    fun `분위기 취향을 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberMoodPreference(
            member = member,
            moodType = MoodType.COZY
        )

        // when
        val savedPreference = memberMoodPreferenceRepository.save(preference)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundPreference = memberMoodPreferenceRepository.findById(savedPreference.id).orElse(null)

        // then
        assertThat(foundPreference).isNotNull
        assertThat(foundPreference.moodType).isEqualTo(MoodType.COZY)
        assertThat(foundPreference.member.id).isEqualTo(member.id)
    }

    @Test
    @DisplayName("회원의 모든 분위기 취향을 조회한다")
    fun `회원의 모든 분위기 취향을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberMoodPreference(member = member, moodType = MoodType.KITSCH)
        val preference2 = MemberMoodPreference(member = member, moodType = MoodType.FRESH)
        val preference3 = MemberMoodPreference(member = member, moodType = MoodType.Y2K)

        memberMoodPreferenceRepository.saveAll(listOf(preference1, preference2, preference3))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val preferences = memberMoodPreferenceRepository.findByMember(member)

        // then
        assertThat(preferences).hasSize(3)
        assertThat(preferences.map { it.moodType })
            .containsExactlyInAnyOrder(MoodType.KITSCH, MoodType.FRESH, MoodType.Y2K)
    }

    @Test
    @DisplayName("회원의 분위기 취향을 모두 삭제한다")
    fun `회원의 분위기 취향을 모두 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberMoodPreference(member = member, moodType = MoodType.DECADENT)
        val preference2 = MemberMoodPreference(member = member, moodType = MoodType.SILLY)

        memberMoodPreferenceRepository.saveAll(listOf(preference1, preference2))
        testEntityManager.flush()

        // when
        memberMoodPreferenceRepository.deleteByMember(member)
        testEntityManager.flush()
        testEntityManager.clear()

        // then
        val preferences = memberMoodPreferenceRepository.findByMember(member)
        assertThat(preferences).isEmpty()
    }

    @Test
    @DisplayName("회원과 분위기 타입으로 존재 여부를 확인한다")
    fun `회원과 분위기 타입으로 존재 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberMoodPreference(member = member, moodType = MoodType.PIXEL_ART)
        memberMoodPreferenceRepository.save(preference)
        testEntityManager.flush()

        // when
        val exists = memberMoodPreferenceRepository.existsByMemberAndMoodType(member, MoodType.PIXEL_ART)
        val notExists = memberMoodPreferenceRepository.existsByMemberAndMoodType(member, MoodType.FUNNY)

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
