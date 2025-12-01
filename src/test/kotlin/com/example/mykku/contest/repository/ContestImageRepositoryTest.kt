package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("ContestImageRepository 테스트")
class ContestImageRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var contestImageRepository: ContestImageRepository

    @Test
    @DisplayName("콘테스트 이미지를 저장하고 조회한다")
    fun `콘테스트 이미지를 저장하고 조회한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val image = ContestImage(url = "https://example.com/image.jpg", orderIndex = 0, contest = contest)

        // when
        val savedImage = contestImageRepository.save(image)
        val foundImage = contestImageRepository.findById(savedImage.id!!).orElse(null)

        // then
        assertThat(foundImage).isNotNull
        assertThat(foundImage.url).isEqualTo("https://example.com/image.jpg")
        assertThat(foundImage.orderIndex).isEqualTo(0)
        assertThat(foundImage.contest.id).isEqualTo(contest.id)
    }

    @Test
    @DisplayName("여러 콘테스트의 이미지를 한번에 조회한다")
    fun `여러 콘테스트의 이미지를 한번에 조회한다`() {
        // given
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest3 = contestRepository.save(
            Contest(title = "콘테스트3", expiredAt = LocalDateTime.now().plusDays(7))
        )

        contestImageRepository.saveAll(listOf(
            ContestImage(url = "url1-1", orderIndex = 0, contest = contest1),
            ContestImage(url = "url1-2", orderIndex = 1, contest = contest1),
            ContestImage(url = "url2-1", orderIndex = 0, contest = contest2)
        ))

        // when
        val result = contestImageRepository.findByContestIn(listOf(contest1, contest2))

        // then
        assertThat(result).hasSize(3)
        assertThat(result.filter { it.contest.id == contest1.id }).hasSize(2)
        assertThat(result.filter { it.contest.id == contest2.id }).hasSize(1)
    }

    @Test
    @DisplayName("특정 콘테스트만 조회하면 해당 콘테스트 이미지만 반환한다")
    fun `특정 콘테스트만 조회하면 해당 콘테스트 이미지만 반환한다`() {
        // given
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        )

        contestImageRepository.saveAll(listOf(
            ContestImage(url = "url1-1", orderIndex = 0, contest = contest1),
            ContestImage(url = "url2-1", orderIndex = 0, contest = contest2)
        ))

        // when
        val result = contestImageRepository.findByContestIn(listOf(contest1))

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].url).isEqualTo("url1-1")
    }

    @Test
    @DisplayName("빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다")
    fun `빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestImageRepository.save(ContestImage(url = "url1", orderIndex = 0, contest = contest))

        // when
        val result = contestImageRepository.findByContestIn(emptyList())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("이미지가 없는 콘테스트를 조회하면 빈 결과를 반환한다")
    fun `이미지가 없는 콘테스트를 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "이미지 없는 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )

        // when
        val result = contestImageRepository.findByContestIn(listOf(contest))

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("여러 이미지를 한번에 저장한다")
    fun `여러 이미지를 한번에 저장한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val images = (0..4).map { i ->
            ContestImage(url = "url$i", orderIndex = i, contest = contest)
        }

        // when
        val savedImages = contestImageRepository.saveAll(images)

        // then
        assertThat(savedImages).hasSize(5)
        savedImages.forEachIndexed { index, image ->
            assertThat(image.url).isEqualTo("url$index")
            assertThat(image.orderIndex).isEqualTo(index)
        }
    }
}
