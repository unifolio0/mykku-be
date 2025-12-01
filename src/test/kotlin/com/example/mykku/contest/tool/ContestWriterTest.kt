package com.example.mykku.contest.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.dto.ContestImageRequest
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
import java.time.LocalDateTime

@DisplayName("ContestWriter 테스트")
class ContestWriterTest : BaseToolTest() {

    @Mock
    private lateinit var contestRepository: ContestRepository

    @Mock
    private lateinit var contestImageRepository: ContestImageRepository

    @Mock
    private lateinit var contestTagRepository: ContestTagRepository

    @InjectMocks
    private lateinit var contestWriter: ContestWriter

    @Test
    @DisplayName("콘테스트를 생성한다")
    fun `콘테스트를 생성한다`() {
        // given
        val title = "테스트 콘테스트"
        val description = "콘테스트 설명"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = listOf(
            ContestImageRequest(url = "url1", orderIndex = 0),
            ContestImageRequest(url = "url2", orderIndex = 1)
        )
        val tagTitles = listOf("디자인", "개발")

        val savedContest = Contest(id = 1L, title = title, description = description, expiredAt = expiredAt)
        val savedImages = imageRequests.mapIndexed { index, req ->
            ContestImage(id = (index + 1).toLong(), url = req.url, orderIndex = req.orderIndex, contest = savedContest)
        }
        val savedTags = tagTitles.mapIndexed { index, tag ->
            ContestTag(id = (index + 1).toLong(), title = tag, contest = savedContest)
        }

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(savedImages)
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(savedTags)

        // when
        val (contest, images, tags) = contestWriter.createContest(title, description, expiredAt, imageRequests, tagTitles)

        // then
        assertThat(contest.id).isEqualTo(1L)
        assertThat(contest.title).isEqualTo(title)
        assertThat(contest.description).isEqualTo(description)
        assertThat(images).hasSize(2)
        assertThat(tags).hasSize(2)
    }

    @Test
    @DisplayName("이미지와 태그 없이 콘테스트를 생성한다")
    fun `이미지와 태그 없이 콘테스트를 생성한다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)

        val savedContest = Contest(id = 1L, title = title, expiredAt = expiredAt)

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(emptyList())
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(emptyList())

        // when
        val (contest, images, tags) = contestWriter.createContest(title, null, expiredAt, emptyList(), emptyList())

        // then
        assertThat(contest.id).isEqualTo(1L)
        assertThat(images).isEmpty()
        assertThat(tags).isEmpty()
    }

    @Test
    @DisplayName("이미지가 10개 초과시 예외를 던진다")
    fun `이미지가 10개 초과시 예외를 던진다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = (0..10).map { ContestImageRequest(url = "url$it", orderIndex = it) }

        // when & then
        val exception = assertThrows<ContestException> {
            contestWriter.createContest(title, null, expiredAt, imageRequests, emptyList())
        }
        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_IMAGE_LIMIT_EXCEEDED)
    }

    @Test
    @DisplayName("이미지가 정확히 10개일 때 정상 생성된다")
    fun `이미지가 정확히 10개일 때 정상 생성된다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = (0..9).map { ContestImageRequest(url = "url$it", orderIndex = it) }

        val savedContest = Contest(id = 1L, title = title, expiredAt = expiredAt)
        val savedImages = imageRequests.mapIndexed { index, req ->
            ContestImage(id = (index + 1).toLong(), url = req.url, orderIndex = req.orderIndex, contest = savedContest)
        }

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(savedImages)
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(emptyList())

        // when
        val (contest, images, tags) = contestWriter.createContest(title, null, expiredAt, imageRequests, emptyList())

        // then
        assertThat(contest.id).isEqualTo(1L)
        assertThat(images).hasSize(10)
    }

    @Test
    @DisplayName("태그가 7개 초과시 예외를 던진다")
    fun `태그가 7개 초과시 예외를 던진다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val tagTitles = (1..8).map { "태그$it" }

        // when & then
        val exception = assertThrows<ContestException> {
            contestWriter.createContest(title, null, expiredAt, emptyList(), tagTitles)
        }
        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_TAG_LIMIT_EXCEEDED)
    }

    @Test
    @DisplayName("태그가 정확히 7개일 때 정상 생성된다")
    fun `태그가 정확히 7개일 때 정상 생성된다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val tagTitles = (1..7).map { "태그$it" }

        val savedContest = Contest(id = 1L, title = title, expiredAt = expiredAt)
        val savedTags = tagTitles.mapIndexed { index, tag ->
            ContestTag(id = (index + 1).toLong(), title = tag, contest = savedContest)
        }

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(emptyList())
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(savedTags)

        // when
        val (contest, images, tags) = contestWriter.createContest(title, null, expiredAt, emptyList(), tagTitles)

        // then
        assertThat(contest.id).isEqualTo(1L)
        assertThat(tags).hasSize(7)
    }

    @Test
    @DisplayName("중복 태그는 제거된다")
    fun `중복 태그는 제거된다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val tagTitles = listOf("디자인", "디자인", "개발", "개발", "기획")

        val savedContest = Contest(id = 1L, title = title, expiredAt = expiredAt)
        val uniqueTags = listOf("디자인", "개발", "기획")
        val savedTags = uniqueTags.mapIndexed { index, tag ->
            ContestTag(id = (index + 1).toLong(), title = tag, contest = savedContest)
        }

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(emptyList())
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(savedTags)

        // when
        val (contest, images, tags) = contestWriter.createContest(title, null, expiredAt, emptyList(), tagTitles)

        // then
        assertThat(tags).hasSize(3)
        assertThat(tags.map { it.title }).containsExactlyInAnyOrder("디자인", "개발", "기획")
    }

    @Test
    @DisplayName("빈 태그와 공백 태그는 무시된다")
    fun `빈 태그와 공백 태그는 무시된다`() {
        // given
        val title = "테스트 콘테스트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val tagTitles = listOf("디자인", "", "  ", "개발")

        val savedContest = Contest(id = 1L, title = title, expiredAt = expiredAt)
        val validTags = listOf("디자인", "개발")
        val savedTags = validTags.mapIndexed { index, tag ->
            ContestTag(id = (index + 1).toLong(), title = tag, contest = savedContest)
        }

        whenever(contestRepository.save(any<Contest>())).thenReturn(savedContest)
        whenever(contestImageRepository.saveAll(any<List<ContestImage>>())).thenReturn(emptyList())
        whenever(contestTagRepository.saveAll(any<List<ContestTag>>())).thenReturn(savedTags)

        // when
        val (contest, images, tags) = contestWriter.createContest(title, null, expiredAt, emptyList(), tagTitles)

        // then
        assertThat(tags).hasSize(2)
        assertThat(tags.map { it.title }).containsExactlyInAnyOrder("디자인", "개발")
    }
}
