package com.example.mykku.preference.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.preference.application.port.output.GenrePreferenceRepository
import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("GenrePreferenceRepositoryAdapter 통합 테스트")
class GenrePreferenceRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var genrePreferenceRepository: GenrePreferenceRepository

    @Nested
    @DisplayName("saveAll 메서드")
    inner class SaveAll {

        @Test
        @DisplayName("장르 선호도 목록을 저장한다")
        fun `장르 선호도 목록 저장 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = listOf(
                MemberGenrePreference.create(member.id, GenreType.KPOP),
                MemberGenrePreference.create(member.id, GenreType.DRAMA_MOVIE)
            )

            val saved = genrePreferenceRepository.saveAll(preferences)

            assertThat(saved).hasSize(2)
            assertThat(saved[0].id.value).isNotEqualTo(0L)
            assertThat(saved[1].id.value).isNotEqualTo(0L)
            assertThat(saved.map { it.genreType }).containsExactlyInAnyOrder(
                GenreType.KPOP,
                GenreType.DRAMA_MOVIE
            )
        }

        @Test
        @DisplayName("빈 목록을 저장하면 빈 목록을 반환한다")
        fun `빈 목록 저장 - 빈 결과 반환`() {
            val saved = genrePreferenceRepository.saveAll(emptyList())

            assertThat(saved).isEmpty()
        }

        @Test
        @DisplayName("모든 장르 타입을 저장할 수 있다")
        fun `모든 장르 타입 저장 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = GenreType.entries.map { genreType ->
                MemberGenrePreference.create(member.id, genreType)
            }

            val saved = genrePreferenceRepository.saveAll(preferences)

            assertThat(saved).hasSize(GenreType.entries.size)
            assertThat(saved.map { it.genreType }).containsExactlyInAnyOrderElementsOf(GenreType.entries)
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원의 장르 선호도 목록을 조회한다")
        fun `회원별 장르 선호도 조회 - 정상 케이스`() {
            val member = createAndSaveMember()
            val preferences = listOf(
                MemberGenrePreference.create(member.id, GenreType.KPOP),
                MemberGenrePreference.create(member.id, GenreType.GAME_ESPORTS),
                MemberGenrePreference.create(member.id, GenreType.BAND_ROCK)
            )
            genrePreferenceRepository.saveAll(preferences)

            val found = genrePreferenceRepository.findByMemberId(member.id)

            assertThat(found).hasSize(3)
            assertThat(found.map { it.genreType }).containsExactlyInAnyOrder(
                GenreType.KPOP,
                GenreType.GAME_ESPORTS,
                GenreType.BAND_ROCK
            )
        }

        @Test
        @DisplayName("선호도가 없는 회원 조회 시 빈 목록을 반환한다")
        fun `선호도 없는 회원 조회 - 빈 목록 반환`() {
            val member = createAndSaveMember()

            val found = genrePreferenceRepository.findByMemberId(member.id)

            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 조회되지 않는다")
        fun `다른 회원 선호도 조회 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "genre_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "genre_m2", email = "member2@test.com", socialId = "member2_s")
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member1.id, GenreType.KPOP)
            ))
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member2.id, GenreType.DRAMA_MOVIE)
            ))

            val found = genrePreferenceRepository.findByMemberId(member1.id)

            assertThat(found).hasSize(1)
            assertThat(found[0].genreType).isEqualTo(GenreType.KPOP)
        }
    }

    @Nested
    @DisplayName("deleteByMemberId 메서드")
    inner class DeleteByMemberId {

        @Test
        @DisplayName("회원의 모든 장르 선호도를 삭제한다")
        fun `회원별 장르 선호도 삭제 - 정상 케이스`() {
            val member = createAndSaveMember()
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member.id, GenreType.KPOP),
                MemberGenrePreference.create(member.id, GenreType.DRAMA_MOVIE)
            ))

            genrePreferenceRepository.deleteByMemberId(member.id)

            val found = genrePreferenceRepository.findByMemberId(member.id)
            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("선호도가 없는 회원 삭제 시 예외가 발생하지 않는다")
        fun `선호도 없는 회원 삭제 - 예외 없음`() {
            val member = createAndSaveMember()

            genrePreferenceRepository.deleteByMemberId(member.id)

            val found = genrePreferenceRepository.findByMemberId(member.id)
            assertThat(found).isEmpty()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 삭제되지 않는다")
        fun `다른 회원 선호도 삭제 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "genre_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "genre_m2", email = "member2@test.com", socialId = "member2_s")
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member1.id, GenreType.KPOP)
            ))
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member2.id, GenreType.DRAMA_MOVIE)
            ))

            genrePreferenceRepository.deleteByMemberId(member1.id)

            val member1Preferences = genrePreferenceRepository.findByMemberId(member1.id)
            val member2Preferences = genrePreferenceRepository.findByMemberId(member2.id)
            assertThat(member1Preferences).isEmpty()
            assertThat(member2Preferences).hasSize(1)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndGenreType 메서드")
    inner class ExistsByMemberIdAndGenreType {

        @Test
        @DisplayName("회원의 특정 장르 선호도가 존재하면 true를 반환한다")
        fun `선호도 존재 확인 - true 반환`() {
            val member = createAndSaveMember()
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member.id, GenreType.KPOP)
            ))

            val exists = genrePreferenceRepository.existsByMemberIdAndGenreType(member.id, GenreType.KPOP)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("회원의 특정 장르 선호도가 없으면 false를 반환한다")
        fun `선호도 미존재 확인 - false 반환`() {
            val member = createAndSaveMember()
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member.id, GenreType.KPOP)
            ))

            val exists = genrePreferenceRepository.existsByMemberIdAndGenreType(member.id, GenreType.DRAMA_MOVIE)

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("선호도가 없는 회원 확인 시 false를 반환한다")
        fun `선호도 없는 회원 확인 - false 반환`() {
            val member = createAndSaveMember()

            val exists = genrePreferenceRepository.existsByMemberIdAndGenreType(member.id, GenreType.KPOP)

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("다른 회원의 선호도는 확인되지 않는다")
        fun `다른 회원 선호도 확인 불가 - 격리 검증`() {
            val member1 = createAndSaveMember(memberId = "genre_m1", email = "member1@test.com", socialId = "member1_s")
            val member2 = createAndSaveMember(memberId = "genre_m2", email = "member2@test.com", socialId = "member2_s")
            genrePreferenceRepository.saveAll(listOf(
                MemberGenrePreference.create(member1.id, GenreType.KPOP)
            ))

            val existsForMember2 = genrePreferenceRepository.existsByMemberIdAndGenreType(member2.id, GenreType.KPOP)

            assertThat(existsForMember2).isFalse()
        }
    }
}
