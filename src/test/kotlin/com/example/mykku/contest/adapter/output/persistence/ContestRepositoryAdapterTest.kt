package com.example.mykku.contest.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.repository.ContestJpaRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.domain.entity.Contest
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestSortType
import com.example.mykku.contest.domain.vo.ContestStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("ContestRepositoryAdapter 통합 테스트")
class ContestRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var contestJpaRepository: ContestJpaRepository

    private fun createAndSaveContest(
        title: String = "테스트 콘테스트",
        description: String? = "테스트 설명",
        startedAt: LocalDateTime = LocalDateTime.now().minusDays(1),
        expiredAt: LocalDateTime = LocalDateTime.now().plusDays(7),
        scrapCount: Int = 0,
        status: ContestStatusType = ContestStatusType.ACTIVE
    ): ContestJpaEntity {
        val contest = ContestJpaEntity(
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt,
            scrapCount = scrapCount,
            status = status,
            thumbnailUrl = "https://example.com/thumbnail.jpg"
        )
        return contestJpaRepository.save(contest)
    }

    @Nested
    @DisplayName("save 메서드")
    inner class SaveTest {

        @Test
        @DisplayName("새로운 콘테스트를 저장할 수 있다")
        fun saveNewContest() {
            val contest = Contest.create(
                title = "새 콘테스트",
                description = "콘테스트 설명",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.now().plusDays(7),
                thumbnailUrl = "https://example.com/thumbnail.jpg"
            )

            val saved = contestRepository.save(contest)

            assertThat(saved.id.value).isGreaterThan(0)
            assertThat(saved.title).isEqualTo("새 콘테스트")
            assertThat(saved.description).isEqualTo("콘테스트 설명")
            assertThat(saved.status).isEqualTo(ContestStatusType.ACTIVE)
        }

        @Test
        @DisplayName("기존 콘테스트를 업데이트할 수 있다")
        fun updateExistingContest() {
            val savedEntity = createAndSaveContest(title = "원본 제목")
            val contest = savedEntity.toDomain()
            contest.updateStatus(ContestStatusType.WINNER_SELECTING)

            val updated = contestRepository.save(contest)

            assertThat(updated.id).isEqualTo(contest.id)
            assertThat(updated.status).isEqualTo(ContestStatusType.WINNER_SELECTING)
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    inner class FindByIdTest {

        @Test
        @DisplayName("ID로 콘테스트를 조회할 수 있다")
        fun findById() {
            val savedEntity = createAndSaveContest(title = "조회할 콘테스트")

            val found = contestRepository.findById(ContestId(savedEntity.id!!))

            assertThat(found).isNotNull
            assertThat(found!!.title).isEqualTo("조회할 콘테스트")
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 null을 반환한다")
        fun findByIdNotFound() {
            val found = contestRepository.findById(ContestId(999999L))

            assertThat(found).isNull()
        }
    }

    @Nested
    @DisplayName("findByStatus 메서드")
    inner class FindByStatusTest {

        @Test
        @DisplayName("상태별로 콘테스트 목록을 조회할 수 있다")
        fun findByStatus() {
            createAndSaveContest(title = "활성 콘테스트1", status = ContestStatusType.ACTIVE)
            createAndSaveContest(title = "활성 콘테스트2", status = ContestStatusType.ACTIVE)
            createAndSaveContest(title = "만료 콘테스트", status = ContestStatusType.EXPIRED)

            val activeContests = contestRepository.findByStatus(ContestStatusType.ACTIVE)

            assertThat(activeContests).hasSize(2)
            assertThat(activeContests.map { it.title }).containsExactlyInAnyOrder("활성 콘테스트1", "활성 콘테스트2")
        }

        @Test
        @DisplayName("해당 상태의 콘테스트가 없으면 빈 목록을 반환한다")
        fun findByStatusEmpty() {
            createAndSaveContest(title = "활성 콘테스트", status = ContestStatusType.ACTIVE)

            val expiredContests = contestRepository.findByStatus(ContestStatusType.EXPIRED)

            assertThat(expiredContests).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByExpiredAtAfter 메서드")
    inner class FindByExpiredAtAfterTest {

        @Test
        @DisplayName("만료 시간 이후인 콘테스트 목록을 조회할 수 있다")
        fun findByExpiredAtAfter() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "진행중 콘테스트", expiredAt = now.plusDays(7))
            createAndSaveContest(title = "만료된 콘테스트", expiredAt = now.minusDays(1))

            val activeContests = contestRepository.findByExpiredAtAfter(now)

            assertThat(activeContests).hasSize(1)
            assertThat(activeContests[0].title).isEqualTo("진행중 콘테스트")
        }
    }

    @Nested
    @DisplayName("findByStatusAndExpiredAtAfter 메서드")
    inner class FindByStatusAndExpiredAtAfterTest {

        @Test
        @DisplayName("상태와 만료 시간 조건으로 콘테스트를 조회할 수 있다")
        fun findByStatusAndExpiredAtAfter() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "활성 진행중", status = ContestStatusType.ACTIVE, expiredAt = now.plusDays(7))
            createAndSaveContest(title = "활성 만료됨", status = ContestStatusType.ACTIVE, expiredAt = now.minusDays(1))
            createAndSaveContest(title = "만료 상태", status = ContestStatusType.EXPIRED, expiredAt = now.plusDays(7))

            val results = contestRepository.findByStatusAndExpiredAtAfter(ContestStatusType.ACTIVE, now)

            assertThat(results).hasSize(1)
            assertThat(results[0].title).isEqualTo("활성 진행중")
        }
    }

    @Nested
    @DisplayName("findWithPagination 메서드")
    inner class FindWithPaginationTest {

        @Test
        @DisplayName("ALL 상태로 전체 콘테스트를 페이지네이션으로 조회할 수 있다")
        fun findWithPaginationAll() {
            createAndSaveContest(title = "콘테스트1")
            createAndSaveContest(title = "콘테스트2")
            createAndSaveContest(title = "콘테스트3")

            val page = contestRepository.findWithPagination(
                status = ContestStatusType.ALL,
                sortType = ContestSortType.LATEST,
                pageable = PageRequest.of(0, 10),
                currentTime = LocalDateTime.now()
            )

            assertThat(page.content).hasSize(3)
            assertThat(page.totalElements).isEqualTo(3)
        }

        @Test
        @DisplayName("ACTIVE 상태를 LATEST 정렬로 조회할 수 있다")
        fun findWithPaginationActiveLatest() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "콘테스트1", expiredAt = now.plusDays(7))
            createAndSaveContest(title = "콘테스트2", expiredAt = now.plusDays(7))
            createAndSaveContest(title = "만료된 콘테스트", expiredAt = now.minusDays(1))

            val page = contestRepository.findWithPagination(
                status = ContestStatusType.ACTIVE,
                sortType = ContestSortType.LATEST,
                pageable = PageRequest.of(0, 10),
                currentTime = now
            )

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("ACTIVE 상태를 OLDEST 정렬로 조회할 수 있다")
        fun findWithPaginationActiveOldest() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "콘테스트1", expiredAt = now.plusDays(7))
            createAndSaveContest(title = "콘테스트2", expiredAt = now.plusDays(7))

            val page = contestRepository.findWithPagination(
                status = ContestStatusType.ACTIVE,
                sortType = ContestSortType.OLDEST,
                pageable = PageRequest.of(0, 10),
                currentTime = now
            )

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("ACTIVE 상태를 POPULAR 정렬로 조회할 수 있다")
        fun findWithPaginationActivePopular() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "인기 콘테스트", scrapCount = 100, expiredAt = now.plusDays(7))
            createAndSaveContest(title = "일반 콘테스트", scrapCount = 10, expiredAt = now.plusDays(7))

            val page = contestRepository.findWithPagination(
                status = ContestStatusType.ACTIVE,
                sortType = ContestSortType.POPULAR,
                pageable = PageRequest.of(0, 10),
                currentTime = now
            )

            assertThat(page.content).hasSize(2)
            assertThat(page.content[0].scrapCount).isGreaterThanOrEqualTo(page.content[1].scrapCount)
        }

        @Test
        @DisplayName("EXPIRED 상태로 만료된 콘테스트를 조회할 수 있다")
        fun findWithPaginationExpired() {
            val now = LocalDateTime.now()
            createAndSaveContest(title = "만료된 콘테스트", expiredAt = now.minusDays(1))
            createAndSaveContest(title = "진행중 콘테스트", expiredAt = now.plusDays(7))

            val page = contestRepository.findWithPagination(
                status = ContestStatusType.EXPIRED,
                sortType = ContestSortType.LATEST,
                pageable = PageRequest.of(0, 10),
                currentTime = now
            )

            assertThat(page.content).hasSize(1)
            assertThat(page.content[0].title).isEqualTo("만료된 콘테스트")
        }

        @Test
        @DisplayName("페이지네이션이 올바르게 동작한다")
        fun paginationWorks() {
            val now = LocalDateTime.now()
            repeat(15) { index ->
                createAndSaveContest(title = "콘테스트$index", expiredAt = now.plusDays(7))
            }

            val firstPage = contestRepository.findWithPagination(
                status = ContestStatusType.ACTIVE,
                sortType = ContestSortType.LATEST,
                pageable = PageRequest.of(0, 10),
                currentTime = now
            )

            val secondPage = contestRepository.findWithPagination(
                status = ContestStatusType.ACTIVE,
                sortType = ContestSortType.LATEST,
                pageable = PageRequest.of(1, 10),
                currentTime = now
            )

            assertThat(firstPage.content).hasSize(10)
            assertThat(secondPage.content).hasSize(5)
            assertThat(firstPage.totalElements).isEqualTo(15)
        }
    }
}
