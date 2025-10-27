package com.example.mykku.preference.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.MemberGenrePreference
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DisplayName("MemberGenrePreferenceRepository 테스트")
class MemberGenrePreferenceRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberGenrePreferenceRepository: MemberGenrePreferenceRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("장르 취향을 저장하고 조회한다")
    fun `장르 취향을 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberGenrePreference(
            member = member,
            genreType = GenreType.KPOP
        )

        // when
        val savedPreference = memberGenrePreferenceRepository.save(preference)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundPreference = memberGenrePreferenceRepository.findById(savedPreference.id).orElse(null)

        // then
        assertThat(foundPreference).isNotNull
        assertThat(foundPreference.genreType).isEqualTo(GenreType.KPOP)
        assertThat(foundPreference.member.id).isEqualTo(member.id)
    }

    @Test
    @DisplayName("회원의 모든 장르 취향을 조회한다")
    fun `회원의 모든 장르 취향을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberGenrePreference(member = member, genreType = GenreType.KPOP)
        val preference2 = MemberGenrePreference(member = member, genreType = GenreType.GAME_ESPORTS)
        val preference3 = MemberGenrePreference(member = member, genreType = GenreType.DRAMA_MOVIE)

        memberGenrePreferenceRepository.saveAll(listOf(preference1, preference2, preference3))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val preferences = memberGenrePreferenceRepository.findByMember(member)

        // then
        assertThat(preferences).hasSize(3)
        assertThat(preferences.map { it.genreType })
            .containsExactlyInAnyOrder(GenreType.KPOP, GenreType.GAME_ESPORTS, GenreType.DRAMA_MOVIE)
    }

    @Test
    @DisplayName("회원의 장르 취향을 모두 삭제한다")
    fun `회원의 장르 취향을 모두 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberGenrePreference(member = member, genreType = GenreType.KPOP)
        val preference2 = MemberGenrePreference(member = member, genreType = GenreType.BAND_ROCK)

        memberGenrePreferenceRepository.saveAll(listOf(preference1, preference2))
        testEntityManager.flush()

        // when
        memberGenrePreferenceRepository.deleteByMember(member)
        testEntityManager.flush()
        testEntityManager.clear()

        // then
        val preferences = memberGenrePreferenceRepository.findByMember(member)
        assertThat(preferences).isEmpty()
    }

    @Test
    @DisplayName("회원과 장르 타입으로 존재 여부를 확인한다")
    fun `회원과 장르 타입으로 존재 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberGenrePreference(member = member, genreType = GenreType.MANGA_ANIME)
        memberGenrePreferenceRepository.save(preference)
        testEntityManager.flush()

        // when
        val exists = memberGenrePreferenceRepository.existsByMemberAndGenreType(member, GenreType.MANGA_ANIME)
        val notExists = memberGenrePreferenceRepository.existsByMemberAndGenreType(member, GenreType.KPOP)

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
