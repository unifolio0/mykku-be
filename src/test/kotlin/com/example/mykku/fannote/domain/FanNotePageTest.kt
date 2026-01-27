package com.example.mykku.fannote.domain

import com.example.mykku.fannote.domain.entity.FanNotePage
import com.example.mykku.fannote.domain.vo.FanNotePageId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("FanNotePage 도메인 엔티티 테스트")
class FanNotePageTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 팬노트 페이지를 생성한다")
        fun `팬노트 페이지 생성 - 정상 케이스`() {
            val fanNotePage = FanNotePage.create(
                fanNoteId = 1L,
                pageNumber = 1,
                imageUrl = "https://example.com/page1.jpg"
            )

            assertThat(fanNotePage.id.value).isEqualTo(0L)
            assertThat(fanNotePage.fanNoteId).isEqualTo(1L)
            assertThat(fanNotePage.pageNumber).isEqualTo(1)
            assertThat(fanNotePage.imageUrl).isEqualTo("https://example.com/page1.jpg")
        }

        @Test
        @DisplayName("팬노트 페이지 생성시 createdAt과 updatedAt이 설정된다")
        fun `팬노트 페이지 생성 - 시간 설정 검증`() {
            val beforeCreation = LocalDateTime.now().minusSeconds(1)

            val fanNotePage = createFanNotePage()

            val afterCreation = LocalDateTime.now().plusSeconds(1)

            assertThat(fanNotePage.createdAt).isNotNull()
            assertThat(fanNotePage.updatedAt).isNotNull()
            assertThat(fanNotePage.createdAt).isEqualTo(fanNotePage.updatedAt)
            assertThat(fanNotePage.createdAt).isAfter(beforeCreation)
            assertThat(fanNotePage.createdAt).isBefore(afterCreation)
        }

        @Test
        @DisplayName("팬노트 페이지 생성시 id는 0으로 설정된다")
        fun `팬노트 페이지 생성 - 초기 id 값`() {
            val fanNotePage = createFanNotePage()

            assertThat(fanNotePage.id).isEqualTo(FanNotePageId(0L))
        }

        @Test
        @DisplayName("다양한 페이지 번호로 팬노트 페이지를 생성한다")
        fun `팬노트 페이지 생성 - 다양한 페이지 번호`() {
            val page1 = FanNotePage.create(fanNoteId = 1L, pageNumber = 1, imageUrl = "https://example.com/1.jpg")
            val page5 = FanNotePage.create(fanNoteId = 1L, pageNumber = 5, imageUrl = "https://example.com/5.jpg")
            val page100 = FanNotePage.create(fanNoteId = 1L, pageNumber = 100, imageUrl = "https://example.com/100.jpg")

            assertThat(page1.pageNumber).isEqualTo(1)
            assertThat(page5.pageNumber).isEqualTo(5)
            assertThat(page100.pageNumber).isEqualTo(100)
        }

        @Test
        @DisplayName("다른 fanNoteId를 가진 페이지들을 생성한다")
        fun `팬노트 페이지 생성 - 다른 fanNoteId`() {
            val pageForNote1 = FanNotePage.create(fanNoteId = 1L, pageNumber = 1, imageUrl = "https://example.com/1.jpg")
            val pageForNote2 = FanNotePage.create(fanNoteId = 2L, pageNumber = 1, imageUrl = "https://example.com/2.jpg")

            assertThat(pageForNote1.fanNoteId).isEqualTo(1L)
            assertThat(pageForNote2.fanNoteId).isEqualTo(2L)
        }
    }

    @Nested
    @DisplayName("updatePage 메서드")
    inner class UpdatePage {

        @Test
        @DisplayName("페이지 번호를 수정한다")
        fun `팬노트 페이지 수정 - 페이지 번호 수정`() {
            val fanNotePage = createReconstitutedFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = 5,
                imageUrl = fanNotePage.imageUrl
            )

            assertThat(updatedPage.pageNumber).isEqualTo(5)
            assertThat(updatedPage.imageUrl).isEqualTo(fanNotePage.imageUrl)
        }

        @Test
        @DisplayName("이미지 URL을 수정한다")
        fun `팬노트 페이지 수정 - 이미지 URL 수정`() {
            val fanNotePage = createReconstitutedFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = fanNotePage.pageNumber,
                imageUrl = "https://example.com/new-image.jpg"
            )

            assertThat(updatedPage.pageNumber).isEqualTo(fanNotePage.pageNumber)
            assertThat(updatedPage.imageUrl).isEqualTo("https://example.com/new-image.jpg")
        }

        @Test
        @DisplayName("페이지 번호와 이미지 URL을 모두 수정한다")
        fun `팬노트 페이지 수정 - 전체 수정`() {
            val fanNotePage = createReconstitutedFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = 10,
                imageUrl = "https://example.com/updated.jpg"
            )

            assertThat(updatedPage.pageNumber).isEqualTo(10)
            assertThat(updatedPage.imageUrl).isEqualTo("https://example.com/updated.jpg")
        }

        @Test
        @DisplayName("수정시 id와 fanNoteId는 유지된다")
        fun `팬노트 페이지 수정 - 불변 필드 유지`() {
            val fanNotePage = createReconstitutedFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = 5,
                imageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedPage.id).isEqualTo(fanNotePage.id)
            assertThat(updatedPage.fanNoteId).isEqualTo(fanNotePage.fanNoteId)
        }

        @Test
        @DisplayName("수정시 createdAt은 유지된다")
        fun `팬노트 페이지 수정 - createdAt 유지`() {
            val fanNotePage = createReconstitutedFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = 5,
                imageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedPage.createdAt).isEqualTo(fanNotePage.createdAt)
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `팬노트 페이지 수정 - 시간 갱신 검증`() {
            val fanNotePage = createFanNotePage()
            val originalUpdatedAt = fanNotePage.updatedAt

            Thread.sleep(10)
            val updatedPage = fanNotePage.updatePage(
                pageNumber = 5,
                imageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedPage.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정시 새로운 인스턴스를 반환한다")
        fun `팬노트 페이지 수정 - 불변성 검증`() {
            val fanNotePage = createFanNotePage()

            val updatedPage = fanNotePage.updatePage(
                pageNumber = 5,
                imageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedPage).isNotSameAs(fanNotePage)
            assertThat(fanNotePage.pageNumber).isEqualTo(1)
            assertThat(fanNotePage.imageUrl).isEqualTo("https://example.com/page1.jpg")
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FanNotePage를 복원한다")
        fun `복원 - 정상 케이스`() {
            val id = FanNotePageId(100L)
            val createdAt = LocalDateTime.of(2024, 5, 20, 10, 30, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 14, 45, 0)

            val fanNotePage = FanNotePage.reconstitute(
                id = id,
                fanNoteId = 50L,
                pageNumber = 3,
                imageUrl = "https://example.com/restored-page.jpg",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(fanNotePage.id).isEqualTo(id)
            assertThat(fanNotePage.id.value).isEqualTo(100L)
            assertThat(fanNotePage.fanNoteId).isEqualTo(50L)
            assertThat(fanNotePage.pageNumber).isEqualTo(3)
            assertThat(fanNotePage.imageUrl).isEqualTo("https://example.com/restored-page.jpg")
            assertThat(fanNotePage.createdAt).isEqualTo(createdAt)
            assertThat(fanNotePage.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 다른 시간으로 복원된다")
        fun `복원 - 시간 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 12, 30, 0)

            val fanNotePage = FanNotePage.reconstitute(
                id = FanNotePageId(1L),
                fanNoteId = 1L,
                pageNumber = 1,
                imageUrl = "https://example.com/page.jpg",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(fanNotePage.createdAt).isEqualTo(createdAt)
            assertThat(fanNotePage.updatedAt).isEqualTo(updatedAt)
            assertThat(fanNotePage.updatedAt).isAfter(fanNotePage.createdAt)
        }

        @Test
        @DisplayName("다양한 페이지 번호로 복원한다")
        fun `복원 - 다양한 페이지 번호`() {
            val now = LocalDateTime.now()

            val page0 = FanNotePage.reconstitute(
                id = FanNotePageId(1L),
                fanNoteId = 1L,
                pageNumber = 0,
                imageUrl = "https://example.com/0.jpg",
                createdAt = now,
                updatedAt = now
            )
            val page999 = FanNotePage.reconstitute(
                id = FanNotePageId(2L),
                fanNoteId = 1L,
                pageNumber = 999,
                imageUrl = "https://example.com/999.jpg",
                createdAt = now,
                updatedAt = now
            )

            assertThat(page0.pageNumber).isEqualTo(0)
            assertThat(page999.pageNumber).isEqualTo(999)
        }
    }

    private fun createFanNotePage(): FanNotePage {
        return FanNotePage.create(
            fanNoteId = 1L,
            pageNumber = 1,
            imageUrl = "https://example.com/page1.jpg"
        )
    }

    private fun createReconstitutedFanNotePage(): FanNotePage {
        return FanNotePage.reconstitute(
            id = FanNotePageId(1L),
            fanNoteId = 1L,
            pageNumber = 1,
            imageUrl = "https://example.com/page1.jpg",
            createdAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0),
            updatedAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0)
        )
    }
}
