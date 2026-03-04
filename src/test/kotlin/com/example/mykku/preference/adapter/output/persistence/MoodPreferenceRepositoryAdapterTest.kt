package com.example.mykku.preference.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.preference.application.port.output.MoodPreferenceRepository
import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("MoodPreferenceRepositoryAdapter 통합 테스트")
class MoodPreferenceRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var moodPreferenceRepository: MoodPreferenceRepository

    @Nested
    @DisplayName("saveAll 메서드")
    inner class SaveAll {

        @Test
        @DisplayName("분위기 선호도 목록을 저장한다")
        fun `분위기 선호도 목록 저장 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = listOf(
                MemberMoodPreference.create(member.id, MoodType.COZY),
                MemberMoodPreference.create(member.id, MoodType.KITSCH)
            )

            val saved = moodPreferenceRepository.saveAll(preferences)

            assertThat(saved).hasSize(2)
            assertThat(saved[0].id.value).isNotEqualTo(0L)
            assertThat(saved[1].id.value).isNotEqualTo(0L)
            assertThat(saved.map { it.moodType }).containsExactlyInAnyOrder(
                MoodType.COZY,
                MoodType.KITSCH
            )
        }

        @Test
        @DisplayName("빈 목록을 저장하면 빈 목록을 반환한다")
        fun `빈 목록 저장 - 빈 결과 반환`() {
            val saved = moodPreferenceRepository.saveAll(emptyList())

            assertThat(saved).isEmpty()
        }

        @Test
        @DisplayName("모든 분위기 타입을 저장할 수 있다")
        fun `모든 분위기 타입 저장 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = MoodType.entries.map { moodType ->
                MemberMoodPreference.create(member.id, moodType)
            }

            val saved = moodPreferenceRepository.saveAll(preferences)

            assertThat(saved).hasSize(MoodType.entries.size)
            assertThat(saved.map { it.moodType }).containsExactlyInAnyOrderElementsOf(MoodType.entries)
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원의 분위기 선호도 목록을 조회한다")
        fun `회원별 분위기 선호도 조회 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = listOf(
                MemberMoodPreference.create(member.id, MoodType.COZY),
                MemberMoodPreference.create(member.id, MoodType.FRESH),
                MemberMoodPreference.create(member.id, MoodType.Y2K)
            )
            moodPreferenceRepository.saveAll(preferences)

            val found = moodPreferenceRepository.findByMemberId(member.id)

            assertThat(found).hasSize(3)
            assertThat(found.map { it.moodType }).containsExactlyInAnyOrder(
                MoodType.COZY,
                MoodType.FRESH,
                MoodType.Y2K
            )
        }

        @Test
        @DisplayName("선호도가 없는 회원 조회 시 빈 목록을 반환한다")
        fun `선호도 없는 회원 조회 - 빈 목록 반환`() {
            val member = createAndSaveMember()

            val found = moodPreferenceRepository.findByMemberId(member.id)

            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 조회되지 않는다")
        fun `다른 회원 선호도 조회 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "mood_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "mood_m2", email = "member2@test.com", socialId = "member2_s")
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member1.id, MoodType.COZY)
            ))
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member2.id, MoodType.KITSCH)
            ))

            val found = moodPreferenceRepository.findByMemberId(member1.id)

            assertThat(found).hasSize(1)
            assertThat(found[0].moodType).isEqualTo(MoodType.COZY)
        }
    }

    @Nested
    @DisplayName("deleteByMemberId 메서드")
    inner class DeleteByMemberId {

        @Test
        @DisplayName("회원의 모든 분위기 선호도를 삭제한다")
        fun `회원별 분위기 선호도 삭제 - 정상 케이스`() {
            val member = createAndSaveMember()
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member.id, MoodType.COZY),
                MemberMoodPreference.create(member.id, MoodType.KITSCH)
            ))

            moodPreferenceRepository.deleteByMemberId(member.id)

            val found = moodPreferenceRepository.findByMemberId(member.id)
            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("선호도가 없는 회원 삭제 시 예외가 발생하지 않는다")
        fun `선호도 없는 회원 삭제 - 예외 없음`() {
            val member = createAndSaveMember()

            moodPreferenceRepository.deleteByMemberId(member.id)

            val found = moodPreferenceRepository.findByMemberId(member.id)
            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 삭제되지 않는다")
        fun `다른 회원 선호도 삭제 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "mood_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "mood_m2", email = "member2@test.com", socialId = "member2_s")
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member1.id, MoodType.COZY)
            ))
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member2.id, MoodType.KITSCH)
            ))

            moodPreferenceRepository.deleteByMemberId(member1.id)

            val member1Preferences = moodPreferenceRepository.findByMemberId(member1.id)
            val member2Preferences = moodPreferenceRepository.findByMemberId(member2.id)
            assertThat(member1Preferences).isEmpty()
            assertThat(member2Preferences).hasSize(1)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndMoodType 메서드")
    inner class ExistsByMemberIdAndMoodType {

        @Test
        @DisplayName("회원의 특정 분위기 선호도가 존재하면 true를 반환한다")
        fun `선호도 존재 확인 - true 반환`() {
            val member = createAndSaveMember()
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member.id, MoodType.COZY)
            ))

            val exists = moodPreferenceRepository.existsByMemberIdAndMoodType(member.id, MoodType.COZY)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("회원의 특정 분위기 선호도가 없으면 false를 반환한다")
        fun `선호도 미존재 확인 - false 반환`() {
            val member = createAndSaveMember()
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member.id, MoodType.COZY)
            ))

            val exists = moodPreferenceRepository.existsByMemberIdAndMoodType(member.id, MoodType.KITSCH)

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("선호도가 없는 회원 확인 시 false를 반환한다")
        fun `선호도 없는 회원 확인 - false 반환`() {
            val member = createAndSaveMember()

            val exists = moodPreferenceRepository.existsByMemberIdAndMoodType(member.id, MoodType.COZY)

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 확인되지 않는다")
        fun `다른 회원 선호도 확인 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "mood_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "mood_m2", email = "member2@test.com", socialId = "member2_s")
            moodPreferenceRepository.saveAll(listOf(
                MemberMoodPreference.create(member1.id, MoodType.COZY)
            ))

            val existsForMember2 = moodPreferenceRepository.existsByMemberIdAndMoodType(member2.id, MoodType.COZY)

            assertThat(existsForMember2).isFalse()
        }
    }
}
