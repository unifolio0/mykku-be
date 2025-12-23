package com.example.mykku.contest

import com.example.mykku.BaseServiceTest
import com.example.mykku.contest.application.port.out.ContestParticipationQueryPort
import com.example.mykku.contest.application.port.out.ContestQueryPort
import com.example.mykku.contest.application.port.out.ContestRepositoryPort
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.dto.ContestDetailResponse
import com.example.mykku.contest.dto.ContestImageRequest
import com.example.mykku.contest.dto.ContestImageResponse
import com.example.mykku.contest.dto.ContestListResponse
import com.example.mykku.contest.dto.CreateContestRequest
import com.example.mykku.contest.application.service.ContestDtoConverter
import com.example.mykku.scrap.application.port.out.SaveContestQueryPort
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@DisplayName("ContestService 테스트")
class ContestServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var contestRepositoryPort: ContestRepositoryPort

    @Mock
    private lateinit var contestQueryPort: ContestQueryPort

    @Mock
    private lateinit var contestParticipationQueryPort: ContestParticipationQueryPort

    @Mock
    private lateinit var saveContestQueryPort: SaveContestQueryPort

    @Mock
    private lateinit var contestDtoConverter: ContestDtoConverter

    @InjectMocks
    private lateinit var contestService: ContestService

    @Test
    @DisplayName("콘테스트를 생성한다")
    fun `콘테스트를 생성한다`() {
        // given
        val request = CreateContestRequest(
            title = "테스트 콘테스트",
            description = "콘테스트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7),
            images = listOf(
                ContestImageRequest(url = "url1", orderIndex = 0),
                ContestImageRequest(url = "url2", orderIndex = 1)
            ),
            tags = listOf("디자인", "개발")
        )

        val contest = Contest(
            id = 1L,
            title = request.title,
            description = request.description,
            startedAt = LocalDateTime.now(),
            expiredAt = request.expiredAt
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

        whenever(contestRepositoryPort.createContest(any(), anyOrNull(), any(), any(), any(), any())).thenReturn(
            Triple(
                contest,
                images,
                tags
            )
        )

        // when
        val result = contestService.createContest(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 콘테스트")
        assertThat(result.description).isEqualTo("콘테스트 설명")
        assertThat(result.images).hasSize(2)
        assertThat(result.tags).hasSize(2)
    }

    @Test
    @DisplayName("이미지와 태그 없이 콘테스트를 생성한다")
    fun `이미지와 태그 없이 콘테스트를 생성한다`() {
        // given
        val request = CreateContestRequest(
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        val contest =
            Contest(id = 1L, title = request.title, startedAt = LocalDateTime.now(), expiredAt = request.expiredAt)
        initializeBaseEntityFields(contest, LocalDateTime.now())

        whenever(contestRepositoryPort.createContest(any(), anyOrNull(), any(), any(), any(), any())).thenReturn(
            Triple(
                contest,
                emptyList(),
                emptyList()
            )
        )

        // when
        val result = contestService.createContest(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.images).isEmpty()
        assertThat(result.tags).isEmpty()
    }

    @Test
    @DisplayName("콘테스트 목록을 조회한다")
    fun `콘테스트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val contest1 = Contest(
            id = 1L,
            title = "콘테스트1",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val contest2 = Contest(
            id = 2L,
            title = "콘테스트2",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val contests = listOf(contest1, contest2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        val image1 = ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest1)
        val imagesByContestId = mapOf(1L to listOf(image1), 2L to emptyList())

        val tag1 = ContestTag(id = 1L, title = "디자인", contest = contest1)
        val tagsByContestId = mapOf(1L to listOf(tag1), 2L to emptyList())

        val response1 = ContestListResponse(
            id = 1L,
            title = "콘테스트1",
            startedAt = LocalDateTime.now(),
            expiredAt = contest1.expiredAt,
            status = ContestStatusType.ACTIVE,
            thumbnailUrl = "url1",
            tags = listOf("디자인"),
            isSaved = true
        )
        val response2 = ContestListResponse(
            id = 2L,
            title = "콘테스트2",
            startedAt = LocalDateTime.now(),
            expiredAt = contest2.expiredAt,
            status = ContestStatusType.ACTIVE,
            thumbnailUrl = null,
            tags = emptyList(),
            isSaved = false
        )

        whenever(contestQueryPort.getContestsWithPagination(any(), any(), any(), any())).thenReturn(page)
        whenever(contestQueryPort.getContestImages(contests)).thenReturn(imagesByContestId)
        whenever(contestQueryPort.getContestTags(contests)).thenReturn(tagsByContestId)
        whenever(saveContestQueryPort.getSavedContestIds(eq(member), any())).thenReturn(setOf(1L))
        whenever(contestDtoConverter.toContestListResponse(eq(contest1), any(), any(), eq(true))).thenReturn(response1)
        whenever(contestDtoConverter.toContestListResponse(eq(contest2), any(), any(), eq(false))).thenReturn(response2)

        // when
        val result = contestService.getContests(ContestStatusType.ACTIVE, ContestSortType.LATEST, 0, 20, member)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }

    @Test
    @DisplayName("콘테스트 상세를 조회한다")
    fun `콘테스트 상세를 조회한다`() {
        // given
        val member = createTestMember()
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
        val imagesByContestId = mapOf(1L to images)

        val tags = listOf(
            ContestTag(id = 1L, title = "디자인", contest = contest)
        )
        val tagsByContestId = mapOf(1L to tags)

        val expectedResponse = ContestDetailResponse(
            id = 1L,
            title = "테스트 콘테스트",
            description = "설명",
            startedAt = LocalDateTime.now(),
            expiredAt = contest.expiredAt,
            status = ContestStatusType.ACTIVE,
            images = listOf(ContestImageResponse(url = "url1", orderIndex = 0)),
            tags = listOf("디자인"),
            isSaved = true,
            createdAt = contest.createdAt
        )

        whenever(contestQueryPort.getContestByIdWithRelations(1L)).thenReturn(contest)
        whenever(contestQueryPort.getContestImages(listOf(contest))).thenReturn(imagesByContestId)
        whenever(contestQueryPort.getContestTags(listOf(contest))).thenReturn(tagsByContestId)
        whenever(saveContestQueryPort.isSaved(member, contest)).thenReturn(true)
        whenever(contestDtoConverter.toContestDetailResponse(contest, images, tags, true)).thenReturn(expectedResponse)

        // when
        val result = contestService.getContestDetail(1L, member)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 콘테스트")
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("저장하지 않은 콘테스트 상세를 조회한다")
    fun `저장하지 않은 콘테스트 상세를 조회한다`() {
        // given
        val member = createTestMember()
        val contest = Contest(
            id = 1L,
            title = "테스트 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(contest, LocalDateTime.now())

        val imagesByContestId = mapOf(1L to emptyList<ContestImage>())
        val tagsByContestId = mapOf(1L to emptyList<ContestTag>())

        val expectedResponse = ContestDetailResponse(
            id = 1L,
            title = "테스트 콘테스트",
            description = null,
            startedAt = LocalDateTime.now(),
            expiredAt = contest.expiredAt,
            status = ContestStatusType.ACTIVE,
            images = emptyList(),
            tags = emptyList(),
            isSaved = false,
            createdAt = contest.createdAt
        )

        whenever(contestQueryPort.getContestByIdWithRelations(1L)).thenReturn(contest)
        whenever(contestQueryPort.getContestImages(listOf(contest))).thenReturn(imagesByContestId)
        whenever(contestQueryPort.getContestTags(listOf(contest))).thenReturn(tagsByContestId)
        whenever(saveContestQueryPort.isSaved(member, contest)).thenReturn(false)
        whenever(contestDtoConverter.toContestDetailResponse(eq(contest), any(), any(), eq(false))).thenReturn(
            expectedResponse
        )

        // when
        val result = contestService.getContestDetail(1L, member)

        // then
        assertThat(result.isSaved).isFalse()
    }

    @Test
    @DisplayName("내가 참여한 콘테스트 목록을 조회한다")
    fun `내가 참여한 콘테스트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val contest1 = Contest(
            id = 1L,
            title = "콘테스트1",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val contest2 = Contest(
            id = 2L,
            title = "콘테스트2",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val contests = listOf(contest1, contest2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        val image1 = ContestImage(id = 1L, url = "url1", orderIndex = 0, contest = contest1)
        val imagesByContestId = mapOf(1L to listOf(image1), 2L to emptyList())

        val tag1 = ContestTag(id = 1L, title = "디자인", contest = contest1)
        val tagsByContestId = mapOf(1L to listOf(tag1), 2L to emptyList())

        val response1 = ContestListResponse(
            id = 1L,
            title = "콘테스트1",
            startedAt = LocalDateTime.now(),
            expiredAt = contest1.expiredAt,
            status = ContestStatusType.ACTIVE,
            thumbnailUrl = "url1",
            tags = listOf("디자인"),
            isSaved = true
        )
        val response2 = ContestListResponse(
            id = 2L,
            title = "콘테스트2",
            startedAt = LocalDateTime.now(),
            expiredAt = contest2.expiredAt,
            status = ContestStatusType.ACTIVE,
            thumbnailUrl = null,
            tags = emptyList(),
            isSaved = false
        )

        whenever(contestParticipationQueryPort.getParticipatedContests(eq(member), any())).thenReturn(page)
        whenever(contestQueryPort.getContestImages(contests)).thenReturn(imagesByContestId)
        whenever(contestQueryPort.getContestTags(contests)).thenReturn(tagsByContestId)
        whenever(saveContestQueryPort.getSavedContestIds(eq(member), any())).thenReturn(setOf(1L))
        whenever(contestDtoConverter.toContestListResponse(eq(contest1), any(), any(), eq(true))).thenReturn(response1)
        whenever(contestDtoConverter.toContestListResponse(eq(contest2), any(), any(), eq(false))).thenReturn(response2)

        // when
        val result = contestService.getMyParticipatedContests(member, 0, 20)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }
}
