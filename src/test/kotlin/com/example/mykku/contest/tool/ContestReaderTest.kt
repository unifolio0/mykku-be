package com.example.mykku.contest.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestImageRepository
import com.example.mykku.contest.repository.ContestRepository
import com.example.mykku.contest.repository.ContestTagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.Optional

@DisplayName("ContestReader 테스트")
class ContestReaderTest : BaseToolTest() {

    @Mock
    private lateinit var contestRepository: ContestRepository

    @Mock
    private lateinit var contestImageRepository: ContestImageRepository

    @Mock
    private lateinit var contestTagRepository: ContestTagRepository

    @InjectMocks
    private lateinit var contestReader: ContestReader

    @Test
    @DisplayName("ID로 콘테스트를 조회한다")
    fun `ID로 콘테스트를 조회한다`() {
        // given
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(contestRepository.findById(1L)).thenReturn(Optional.of(contest))

        // when
        val result = contestReader.getContestById(1L)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 콘테스트")
    }

    @Test
    @DisplayName("존재하지 않는 콘테스트 조회 시 예외를 던진다")
    fun `존재하지 않는 콘테스트 조회 시 예외를 던진다`() {
        // given
        whenever(contestRepository.findById(999L)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows<ContestException> {
            contestReader.getContestById(999L)
        }
        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_NOT_FOUND)
    }

    @Test
    @DisplayName("ACTIVE 상태와 LATEST 정렬로 콘테스트를 페이지네이션 조회한다")
    fun `ACTIVE 상태와 LATEST 정렬로 콘테스트를 페이지네이션 조회한다`() {
        // given
        val contests = listOf(
            Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestRepository.findByExpiredAtAfterOrderByCreatedAtDesc(any(), any())).thenReturn(page)

        // when
        val result = contestReader.getContestsWithPagination(
            ContestStatusType.ACTIVE,
            ContestSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ACTIVE 상태와 OLDEST 정렬로 콘테스트를 조회한다")
    fun `ACTIVE 상태와 OLDEST 정렬로 콘테스트를 조회한다`() {
        // given
        val contests = listOf(
            Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestRepository.findByExpiredAtAfterOrderByCreatedAtAsc(any(), any())).thenReturn(page)

        // when
        val result = contestReader.getContestsWithPagination(
            ContestStatusType.ACTIVE,
            ContestSortType.OLDEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ACTIVE 상태와 POPULAR 정렬로 콘테스트를 조회한다")
    fun `ACTIVE 상태와 POPULAR 정렬로 콘테스트를 조회한다`() {
        // given
        val contests = listOf(
            Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 100)
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestRepository.findActiveContestsByPopular(any(), any())).thenReturn(page)

        // when
        val result = contestReader.getContestsWithPagination(
            ContestStatusType.ACTIVE,
            ContestSortType.POPULAR,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("EXPIRED 상태로 콘테스트를 조회한다")
    fun `EXPIRED 상태로 콘테스트를 조회한다`() {
        // given
        val contests = listOf(
            Contest(id = 1L, title = "만료 콘테스트", expiredAt = LocalDateTime.now().minusDays(1))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(any(), any())).thenReturn(page)

        // when
        val result = contestReader.getContestsWithPagination(
            ContestStatusType.EXPIRED,
            ContestSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ALL 상태로 모든 콘테스트를 조회한다")
    fun `ALL 상태로 모든 콘테스트를 조회한다`() {
        // given
        val contests = listOf(
            Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7)),
            Contest(id = 2L, title = "콘테스트2", expiredAt = LocalDateTime.now().minusDays(1))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestRepository.findAllByOrderByCreatedAtDesc(any())).thenReturn(page)

        // when
        val result = contestReader.getContestsWithPagination(
            ContestStatusType.ALL,
            ContestSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(2)
    }

    @Test
    @DisplayName("콘테스트 이미지를 조회한다")
    fun `콘테스트 이미지를 조회한다`() {
        // given
        val contest1 = Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(id = 2L, title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        val contests = listOf(contest1, contest2)

        val images = listOf(
            ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest1),
            ContestImage(id = 2L, url = "url2", orderIndex = 1, contest = contest1),
            ContestImage(id = 3L, url = "url3", orderIndex = 0, contest = contest2)
        )

        whenever(contestImageRepository.findByContestIn(contests)).thenReturn(images)

        // when
        val result = contestReader.getContestImages(contests)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1L]).hasSize(2)
        assertThat(result[2L]).hasSize(1)
    }

    @Test
    @DisplayName("콘테스트 태그를 조회한다")
    fun `콘테스트 태그를 조회한다`() {
        // given
        val contest1 = Contest(id = 1L, title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(id = 2L, title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        val contests = listOf(contest1, contest2)

        val tags = listOf(
            ContestTag(id = 1L, title = "디자인", contest = contest1),
            ContestTag(id = 2L, title = "개발", contest = contest1),
            ContestTag(id = 3L, title = "기획", contest = contest2)
        )

        whenever(contestTagRepository.findByContestIn(contests)).thenReturn(tags)

        // when
        val result = contestReader.getContestTags(contests)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1L]).hasSize(2)
        assertThat(result[2L]).hasSize(1)
    }

    @Test
    @DisplayName("태그 제목 목록으로 태그를 조회한다")
    fun `태그 제목 목록으로 태그를 조회한다`() {
        // given
        val contest = Contest(id = 1L, title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        val tags = listOf(
            ContestTag(id = 1L, title = "디자인", contest = contest),
            ContestTag(id = 2L, title = "개발", contest = contest)
        )

        whenever(contestTagRepository.findAllByTitleIn(listOf("디자인", "개발"))).thenReturn(tags)

        // when
        val result = contestReader.getContestTagsByTitles(listOf("디자인", "개발"))

        // then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactlyInAnyOrder("디자인", "개발")
    }

    @Test
    @DisplayName("관계와 함께 콘테스트를 조회한다")
    fun `관계와 함께 콘테스트를 조회한다`() {
        // given
        val contest = Contest(id = 1L, title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))

        whenever(contestRepository.findById(1L)).thenReturn(Optional.of(contest))

        // when
        val result = contestReader.getContestByIdWithRelations(1L)

        // then
        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    @DisplayName("관계 조회 시 존재하지 않으면 예외를 던진다")
    fun `관계 조회 시 존재하지 않으면 예외를 던진다`() {
        // given
        whenever(contestRepository.findById(999L)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows<ContestException> {
            contestReader.getContestByIdWithRelations(999L)
        }
        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_NOT_FOUND)
    }
}
