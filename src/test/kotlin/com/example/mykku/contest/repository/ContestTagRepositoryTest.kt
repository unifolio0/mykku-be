package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("ContestTagRepository 테스트")
class ContestTagRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var contestTagRepository: ContestTagRepository

    @Test
    @DisplayName("콘테스트 태그를 저장하고 조회한다")
    fun `콘테스트 태그를 저장하고 조회한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val tag = ContestTag(title = "태그1", contest = contest)

        // when
        val savedTag = contestTagRepository.save(tag)
        val foundTag = contestTagRepository.findById(savedTag.id!!).orElse(null)

        // then
        assertThat(foundTag).isNotNull
        assertThat(foundTag.title).isEqualTo("태그1")
        assertThat(foundTag.contest.id).isEqualTo(contest.id)
    }

    @Test
    @DisplayName("여러 콘테스트의 태그를 한번에 조회한다")
    fun `여러 콘테스트의 태그를 한번에 조회한다`() {
        // given
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        )

        contestTagRepository.saveAll(listOf(
            ContestTag(title = "태그1", contest = contest1),
            ContestTag(title = "태그2", contest = contest1),
            ContestTag(title = "태그3", contest = contest2)
        ))

        // when
        val result = contestTagRepository.findByContestIn(listOf(contest1, contest2))

        // then
        assertThat(result).hasSize(3)
        assertThat(result.filter { it.contest.id == contest1.id }).hasSize(2)
        assertThat(result.filter { it.contest.id == contest2.id }).hasSize(1)
    }

    @Test
    @DisplayName("태그 제목으로 조회한다")
    fun `태그 제목으로 조회한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestTagRepository.saveAll(listOf(
            ContestTag(title = "디자인", contest = contest),
            ContestTag(title = "개발", contest = contest),
            ContestTag(title = "기획", contest = contest)
        ))

        // when
        val result = contestTagRepository.findAllByTitleIn(listOf("디자인", "개발"))

        // then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactlyInAnyOrder("디자인", "개발")
    }

    @Test
    @DisplayName("존재하지 않는 태그 제목으로 조회하면 빈 결과를 반환한다")
    fun `존재하지 않는 태그 제목으로 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestTagRepository.save(ContestTag(title = "디자인", contest = contest))

        // when
        val result = contestTagRepository.findAllByTitleIn(listOf("존재안함", "없는태그"))

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다")
    fun `빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestTagRepository.save(ContestTag(title = "태그", contest = contest))

        // when
        val result = contestTagRepository.findByContestIn(emptyList())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("태그가 없는 콘테스트를 조회하면 빈 결과를 반환한다")
    fun `태그가 없는 콘테스트를 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "태그 없는 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )

        // when
        val result = contestTagRepository.findByContestIn(listOf(contest))

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("영문과 숫자를 포함한 태그를 저장한다")
    fun `영문과 숫자를 포함한 태그를 저장한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )

        // when
        val tags = contestTagRepository.saveAll(listOf(
            ContestTag(title = "Design", contest = contest),
            ContestTag(title = "2024대회", contest = contest),
            ContestTag(title = "ABC123", contest = contest)
        ))

        // then
        assertThat(tags).hasSize(3)
        assertThat(tags.map { it.title }).containsExactlyInAnyOrder("Design", "2024대회", "ABC123")
    }

    @Test
    @DisplayName("여러 태그를 한번에 저장한다")
    fun `여러 태그를 한번에 저장한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val tags = listOf("디자인", "개발", "기획", "마케팅", "영업").map { title ->
            ContestTag(title = title, contest = contest)
        }

        // when
        val savedTags = contestTagRepository.saveAll(tags)

        // then
        assertThat(savedTags).hasSize(5)
    }
}
