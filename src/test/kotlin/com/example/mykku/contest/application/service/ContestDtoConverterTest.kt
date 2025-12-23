package com.example.mykku.contest.application.service

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("ContestDtoConverter 테스트")
class ContestDtoConverterTest : BaseToolTest() {

    private val contestDtoConverter = ContestDtoConverter()

    @Test
    @DisplayName("Contest를 ContestListResponse로 변환한다")
    fun `Contest를 ContestListResponse로 변환한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            description = "설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val images = listOf(
            ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest),
            ContestImage(id = 2L, url = "url2", orderIndex = 1, contest = contest)
        )
        val tags = listOf(
            ContestTag(id = 1L, title = "디자인", contest = contest),
            ContestTag(id = 2L, title = "개발", contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestListResponse(contest, images, tags, true)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 콘테스트")
        assertThat(result.thumbnailUrl).isEqualTo("url1")
        assertThat(result.tags).containsExactlyInAnyOrder("디자인", "개발")
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("이미지가 없을 때 thumbnailUrl은 null이다")
    fun `이미지가 없을 때 thumbnailUrl은 null이다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        // when
        val result = contestDtoConverter.toContestListResponse(contest, emptyList(), emptyList(), false)

        // then
        assertThat(result.thumbnailUrl).isNull()
        assertThat(result.tags).isEmpty()
        assertThat(result.isSaved).isFalse()
    }

    @Test
    @DisplayName("이미지가 orderIndex 순으로 정렬되어 첫 번째가 thumbnail이 된다")
    fun `이미지가 orderIndex 순으로 정렬되어 첫 번째가 thumbnail이 된다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val images = listOf(
            ContestImage(id = 2L, url = "url2", orderIndex = 2, contest = contest),
            ContestImage(id = 1L, url = "url0", orderIndex = 0, contest = contest),
            ContestImage(id = 3L, url = "url1", orderIndex = 1, contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestListResponse(contest, images, emptyList(), false)

        // then
        assertThat(result.thumbnailUrl).isEqualTo("url0")
    }

    @Test
    @DisplayName("Contest를 ContestDetailResponse로 변환한다")
    fun `Contest를 ContestDetailResponse로 변환한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            description = "콘테스트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(contest, LocalDateTime.now())

        val images = listOf(
            ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest),
            ContestImage(id = 2L, url = "url2", orderIndex = 1, contest = contest)
        )
        val tags = listOf(
            ContestTag(id = 1L, title = "디자인", contest = contest),
            ContestTag(id = 2L, title = "개발", contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestDetailResponse(contest, images, tags, true)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 콘테스트")
        assertThat(result.description).isEqualTo("콘테스트 설명")
        assertThat(result.images).hasSize(2)
        assertThat(result.images[0].url).isEqualTo("url1")
        assertThat(result.images[0].orderIndex).isEqualTo(0)
        assertThat(result.tags).containsExactlyInAnyOrder("디자인", "개발")
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("ContestDetailResponse 이미지가 orderIndex 순으로 정렬된다")
    fun `ContestDetailResponse 이미지가 orderIndex 순으로 정렬된다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(contest, LocalDateTime.now())

        val images = listOf(
            ContestImage(id = 3L, url = "url2", orderIndex = 2, contest = contest),
            ContestImage(id = 1L, url = "url0", orderIndex = 0, contest = contest),
            ContestImage(id = 2L, url = "url1", orderIndex = 1, contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestDetailResponse(contest, images, emptyList(), false)

        // then
        assertThat(result.images).hasSize(3)
        assertThat(result.images[0].orderIndex).isEqualTo(0)
        assertThat(result.images[1].orderIndex).isEqualTo(1)
        assertThat(result.images[2].orderIndex).isEqualTo(2)
    }

    @Test
    @DisplayName("이미지 없이 ContestDetailResponse를 생성한다")
    fun `이미지 없이 ContestDetailResponse를 생성한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(contest, LocalDateTime.now())

        // when
        val result = contestDtoConverter.toContestDetailResponse(contest, emptyList(), emptyList(), false)

        // then
        assertThat(result.images).isEmpty()
        assertThat(result.tags).isEmpty()
        assertThat(result.isSaved).isFalse()
    }

    @Test
    @DisplayName("태그 없이 ContestListResponse를 생성한다")
    fun `태그 없이 ContestListResponse를 생성한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val images = listOf(
            ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestListResponse(contest, images, emptyList(), true)

        // then
        assertThat(result.thumbnailUrl).isEqualTo("url1")
        assertThat(result.tags).isEmpty()
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("태그 없이 ContestDetailResponse를 생성한다")
    fun `태그 없이 ContestDetailResponse를 생성한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            description = "설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(contest, LocalDateTime.now())

        val images = listOf(
            ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest)
        )

        // when
        val result = contestDtoConverter.toContestDetailResponse(contest, images, emptyList(), false)

        // then
        assertThat(result.images).hasSize(1)
        assertThat(result.tags).isEmpty()
        assertThat(result.description).isEqualTo("설명")
    }
}
