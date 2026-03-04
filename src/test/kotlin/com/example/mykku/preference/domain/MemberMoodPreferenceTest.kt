package com.example.mykku.preference.domain

import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType
import com.example.mykku.preference.domain.vo.PreferenceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("MemberMoodPreference 도메인 엔티티 테스트")
class MemberMoodPreferenceTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        fun `정상적으로 분위기 선호도를 생성한다`() {
            val preference = MemberMoodPreference.create(
                memberId = 1L,
                moodType = MoodType.COZY
            )

            assertThat(preference.id).isEqualTo(PreferenceId(0))
            assertThat(preference.memberId).isEqualTo(1L)
            assertThat(preference.moodType).isEqualTo(MoodType.COZY)
        }

        @Test
        fun `분위기 선호도 생성시 createdAt과 updatedAt이 설정된다`() {
            val beforeCreate = LocalDateTime.now()

            val preference = MemberMoodPreference.create(
                memberId = 1L,
                moodType = MoodType.KITSCH
            )

            val afterCreate = LocalDateTime.now()

            assertThat(preference.createdAt).isNotNull()
            assertThat(preference.updatedAt).isNotNull()
            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
            assertThat(preference.createdAt).isBetween(beforeCreate, afterCreate)
        }

        @Test
        fun `분위기 선호도 생성시 createdAt과 updatedAt이 동일하다`() {
            val preference = MemberMoodPreference.create(
                memberId = 2L,
                moodType = MoodType.FRESH
            )

            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
        }

        @Test
        fun `모든 분위기 타입으로 생성할 수 있다`() {
            MoodType.entries.forEach { moodType ->
                val preference = MemberMoodPreference.create(
                    memberId = 3L,
                    moodType = moodType
                )

                assertThat(preference.moodType).isEqualTo(moodType)
            }
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        fun `저장된 데이터로 MemberMoodPreference를 복원한다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val preference = MemberMoodPreference.reconstitute(
                id = PreferenceId(1L),
                memberId = 1L,
                moodType = MoodType.DECADENT,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.id).isEqualTo(PreferenceId(1L))
            assertThat(preference.memberId).isEqualTo(1L)
            assertThat(preference.moodType).isEqualTo(MoodType.DECADENT)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        fun `복원된 엔티티의 id는 PreferenceId Value Object로 래핑된다`() {
            val preference = MemberMoodPreference.reconstitute(
                id = PreferenceId(42L),
                memberId = 1L,
                moodType = MoodType.SILLY,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(preference.id).isNotNull()
            assertThat(preference.id.value).isEqualTo(42L)
        }

        @Test
        fun `다양한 id 값으로 복원할 수 있다`() {
            val now = LocalDateTime.now()

            val preference = MemberMoodPreference.reconstitute(
                id = PreferenceId(999L),
                memberId = 999L,
                moodType = MoodType.Y2K,
                createdAt = now,
                updatedAt = now
            )

            assertThat(preference.id.value).isEqualTo(999L)
        }

        @Test
        fun `복원시 모든 분위기 타입을 사용할 수 있다`() {
            val now = LocalDateTime.now()

            MoodType.entries.forEachIndexed { index, moodType ->
                val preference = MemberMoodPreference.reconstitute(
                    id = PreferenceId(index.toLong()),
                    memberId = index.toLong(),
                    moodType = moodType,
                    createdAt = now,
                    updatedAt = now
                )

                assertThat(preference.moodType).isEqualTo(moodType)
            }
        }

        @Test
        fun `복원시 createdAt과 updatedAt이 다를 수 있다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 6, 15, 14, 30, 0)

            val preference = MemberMoodPreference.reconstitute(
                id = PreferenceId(1L),
                memberId = 1L,
                moodType = MoodType.FUNNY,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.createdAt).isBefore(preference.updatedAt)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }
    }
}
