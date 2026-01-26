package com.example.mykku.preference.domain

import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType
import com.example.mykku.preference.domain.vo.PreferenceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("MemberGenrePreference 도메인 엔티티 테스트")
class MemberGenrePreferenceTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        fun `정상적으로 장르 선호도를 생성한다`() {
            val preference = MemberGenrePreference.create(
                memberId = "member-123",
                genreType = GenreType.KPOP
            )

            assertThat(preference.id).isEqualTo(PreferenceId(0))
            assertThat(preference.memberId).isEqualTo("member-123")
            assertThat(preference.genreType).isEqualTo(GenreType.KPOP)
        }

        @Test
        fun `장르 선호도 생성시 createdAt과 updatedAt이 설정된다`() {
            val beforeCreate = LocalDateTime.now()

            val preference = MemberGenrePreference.create(
                memberId = "member-123",
                genreType = GenreType.WEBTOON_WEBNOVEL
            )

            val afterCreate = LocalDateTime.now()

            assertThat(preference.createdAt).isNotNull()
            assertThat(preference.updatedAt).isNotNull()
            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
            assertThat(preference.createdAt).isBetween(beforeCreate, afterCreate)
        }

        @Test
        fun `장르 선호도 생성시 createdAt과 updatedAt이 동일하다`() {
            val preference = MemberGenrePreference.create(
                memberId = "member-456",
                genreType = GenreType.MANGA_ANIME
            )

            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
        }

        @Test
        fun `모든 장르 타입으로 생성할 수 있다`() {
            GenreType.entries.forEach { genreType ->
                val preference = MemberGenrePreference.create(
                    memberId = "member-test",
                    genreType = genreType
                )

                assertThat(preference.genreType).isEqualTo(genreType)
            }
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        fun `저장된 데이터로 MemberGenrePreference를 복원한다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val preference = MemberGenrePreference.reconstitute(
                id = PreferenceId(1L),
                memberId = "member-123",
                genreType = GenreType.GAME_ESPORTS,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.id).isEqualTo(PreferenceId(1L))
            assertThat(preference.memberId).isEqualTo("member-123")
            assertThat(preference.genreType).isEqualTo(GenreType.GAME_ESPORTS)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        fun `복원된 엔티티의 id는 PreferenceId Value Object로 래핑된다`() {
            val preference = MemberGenrePreference.reconstitute(
                id = PreferenceId(42L),
                memberId = "member-123",
                genreType = GenreType.DRAMA_MOVIE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(preference.id).isNotNull()
            assertThat(preference.id.value).isEqualTo(42L)
        }

        @Test
        fun `다양한 id 값으로 복원할 수 있다`() {
            val now = LocalDateTime.now()

            val preference = MemberGenrePreference.reconstitute(
                id = PreferenceId(999L),
                memberId = "member-999",
                genreType = GenreType.THEATER_MUSICAL,
                createdAt = now,
                updatedAt = now
            )

            assertThat(preference.id.value).isEqualTo(999L)
        }

        @Test
        fun `복원시 모든 장르 타입을 사용할 수 있다`() {
            val now = LocalDateTime.now()

            GenreType.entries.forEachIndexed { index, genreType ->
                val preference = MemberGenrePreference.reconstitute(
                    id = PreferenceId(index.toLong()),
                    memberId = "member-$index",
                    genreType = genreType,
                    createdAt = now,
                    updatedAt = now
                )

                assertThat(preference.genreType).isEqualTo(genreType)
            }
        }

        @Test
        fun `복원시 createdAt과 updatedAt이 다를 수 있다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 6, 15, 14, 30, 0)

            val preference = MemberGenrePreference.reconstitute(
                id = PreferenceId(1L),
                memberId = "member-123",
                genreType = GenreType.BAND_ROCK,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.createdAt).isBefore(preference.updatedAt)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }
    }
}
