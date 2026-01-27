package com.example.mykku.fannote.domain

import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.vo.FanNoteId
import java.time.LocalDate
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("FanNote 도메인 엔티티 테스트")
class FanNoteTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("모든 필드를 포함하여 팬노트를 생성한다")
        fun `팬노트 생성 - 모든 필드 포함`() {
            val productionDate = LocalDate.of(2024, 1, 15)

            val fanNote = FanNote.create(
                title = "테스트 팬노트",
                subtitle = "부제목입니다",
                content = "본문 내용입니다",
                productionDate = productionDate,
                coverImageUrl = "https://example.com/cover.jpg"
            )

            assertThat(fanNote.id.value).isEqualTo(0L)
            assertThat(fanNote.title).isEqualTo("테스트 팬노트")
            assertThat(fanNote.subtitle).isEqualTo("부제목입니다")
            assertThat(fanNote.content).isEqualTo("본문 내용입니다")
            assertThat(fanNote.productionDate).isEqualTo(productionDate)
            assertThat(fanNote.coverImageUrl).isEqualTo("https://example.com/cover.jpg")
        }

        @Test
        @DisplayName("선택 필드가 null인 팬노트를 생성한다")
        fun `팬노트 생성 - 선택 필드 null`() {
            val productionDate = LocalDate.of(2024, 1, 15)

            val fanNote = FanNote.create(
                title = "테스트 팬노트",
                subtitle = null,
                content = null,
                productionDate = productionDate,
                coverImageUrl = null
            )

            assertThat(fanNote.title).isEqualTo("테스트 팬노트")
            assertThat(fanNote.subtitle).isNull()
            assertThat(fanNote.content).isNull()
            assertThat(fanNote.productionDate).isEqualTo(productionDate)
            assertThat(fanNote.coverImageUrl).isNull()
        }

        @Test
        @DisplayName("팬노트 생성시 createdAt과 updatedAt이 설정된다")
        fun `팬노트 생성 - 시간 설정 검증`() {
            val beforeCreation = LocalDateTime.now().minusSeconds(1)

            val fanNote = createFanNote()

            val afterCreation = LocalDateTime.now().plusSeconds(1)

            assertThat(fanNote.createdAt).isNotNull()
            assertThat(fanNote.updatedAt).isNotNull()
            assertThat(fanNote.createdAt).isEqualTo(fanNote.updatedAt)
            assertThat(fanNote.createdAt).isAfter(beforeCreation)
            assertThat(fanNote.createdAt).isBefore(afterCreation)
        }

        @Test
        @DisplayName("팬노트 생성시 id는 0으로 설정된다")
        fun `팬노트 생성 - 초기 id 값`() {
            val fanNote = createFanNote()

            assertThat(fanNote.id).isEqualTo(FanNoteId(0L))
        }

        @Test
        @DisplayName("subtitle만 null인 팬노트를 생성한다")
        fun `팬노트 생성 - subtitle만 null`() {
            val productionDate = LocalDate.of(2024, 6, 20)

            val fanNote = FanNote.create(
                title = "제목만 있는 팬노트",
                subtitle = null,
                content = "본문은 있습니다",
                productionDate = productionDate,
                coverImageUrl = "https://example.com/image.jpg"
            )

            assertThat(fanNote.title).isEqualTo("제목만 있는 팬노트")
            assertThat(fanNote.subtitle).isNull()
            assertThat(fanNote.content).isEqualTo("본문은 있습니다")
            assertThat(fanNote.coverImageUrl).isEqualTo("https://example.com/image.jpg")
        }

        @Test
        @DisplayName("content만 null인 팬노트를 생성한다")
        fun `팬노트 생성 - content만 null`() {
            val productionDate = LocalDate.of(2024, 6, 20)

            val fanNote = FanNote.create(
                title = "제목",
                subtitle = "부제목",
                content = null,
                productionDate = productionDate,
                coverImageUrl = "https://example.com/image.jpg"
            )

            assertThat(fanNote.subtitle).isEqualTo("부제목")
            assertThat(fanNote.content).isNull()
        }

        @Test
        @DisplayName("coverImageUrl만 null인 팬노트를 생성한다")
        fun `팬노트 생성 - coverImageUrl만 null`() {
            val productionDate = LocalDate.of(2024, 6, 20)

            val fanNote = FanNote.create(
                title = "제목",
                subtitle = "부제목",
                content = "본문",
                productionDate = productionDate,
                coverImageUrl = null
            )

            assertThat(fanNote.subtitle).isEqualTo("부제목")
            assertThat(fanNote.content).isEqualTo("본문")
            assertThat(fanNote.coverImageUrl).isNull()
        }
    }

    @Nested
    @DisplayName("updateInfo 메서드")
    inner class UpdateInfo {

        @Test
        @DisplayName("모든 필드를 수정한다")
        fun `팬노트 수정 - 모든 필드 수정`() {
            val fanNote = createFanNote()
            val newProductionDate = LocalDate.of(2025, 3, 10)

            val updatedFanNote = fanNote.updateInfo(
                title = "수정된 제목",
                subtitle = "수정된 부제목",
                content = "수정된 본문",
                productionDate = newProductionDate,
                coverImageUrl = "https://example.com/new-cover.jpg"
            )

            assertThat(updatedFanNote.title).isEqualTo("수정된 제목")
            assertThat(updatedFanNote.subtitle).isEqualTo("수정된 부제목")
            assertThat(updatedFanNote.content).isEqualTo("수정된 본문")
            assertThat(updatedFanNote.productionDate).isEqualTo(newProductionDate)
            assertThat(updatedFanNote.coverImageUrl).isEqualTo("https://example.com/new-cover.jpg")
        }

        @Test
        @DisplayName("수정시 id와 createdAt은 유지된다")
        fun `팬노트 수정 - 불변 필드 유지`() {
            val fanNote = createReconstitutedFanNote()

            val updatedFanNote = fanNote.updateInfo(
                title = "수정된 제목",
                subtitle = "수정된 부제목",
                content = "수정된 본문",
                productionDate = LocalDate.of(2025, 1, 1),
                coverImageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedFanNote.id).isEqualTo(fanNote.id)
            assertThat(updatedFanNote.createdAt).isEqualTo(fanNote.createdAt)
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `팬노트 수정 - 시간 갱신 검증`() {
            val fanNote = createFanNote()
            val originalUpdatedAt = fanNote.updatedAt

            Thread.sleep(10)
            val updatedFanNote = fanNote.updateInfo(
                title = "수정된 제목",
                subtitle = null,
                content = null,
                productionDate = LocalDate.now(),
                coverImageUrl = null
            )

            assertThat(updatedFanNote.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("선택 필드를 null로 변경할 수 있다")
        fun `팬노트 수정 - 선택 필드 null로 변경`() {
            val fanNote = createFanNote()

            val updatedFanNote = fanNote.updateInfo(
                title = "새 제목",
                subtitle = null,
                content = null,
                productionDate = LocalDate.of(2025, 1, 1),
                coverImageUrl = null
            )

            assertThat(updatedFanNote.title).isEqualTo("새 제목")
            assertThat(updatedFanNote.subtitle).isNull()
            assertThat(updatedFanNote.content).isNull()
            assertThat(updatedFanNote.coverImageUrl).isNull()
        }

        @Test
        @DisplayName("null이었던 선택 필드에 값을 설정할 수 있다")
        fun `팬노트 수정 - null에서 값으로 변경`() {
            val fanNote = FanNote.create(
                title = "제목",
                subtitle = null,
                content = null,
                productionDate = LocalDate.of(2024, 1, 1),
                coverImageUrl = null
            )

            val updatedFanNote = fanNote.updateInfo(
                title = "제목",
                subtitle = "새로운 부제목",
                content = "새로운 본문",
                productionDate = LocalDate.of(2024, 1, 1),
                coverImageUrl = "https://example.com/new-cover.jpg"
            )

            assertThat(updatedFanNote.subtitle).isEqualTo("새로운 부제목")
            assertThat(updatedFanNote.content).isEqualTo("새로운 본문")
            assertThat(updatedFanNote.coverImageUrl).isEqualTo("https://example.com/new-cover.jpg")
        }

        @Test
        @DisplayName("수정시 새로운 인스턴스를 반환한다")
        fun `팬노트 수정 - 불변성 검증`() {
            val fanNote = createFanNote()

            val updatedFanNote = fanNote.updateInfo(
                title = "수정된 제목",
                subtitle = "수정된 부제목",
                content = "수정된 본문",
                productionDate = LocalDate.of(2025, 1, 1),
                coverImageUrl = "https://example.com/new.jpg"
            )

            assertThat(updatedFanNote).isNotSameAs(fanNote)
            assertThat(fanNote.title).isEqualTo("테스트 팬노트")
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FanNote를 복원한다")
        fun `복원 - 정상 케이스`() {
            val id = FanNoteId(100L)
            val productionDate = LocalDate.of(2024, 5, 20)
            val createdAt = LocalDateTime.of(2024, 5, 20, 10, 30, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 14, 45, 0)

            val fanNote = FanNote.reconstitute(
                id = id,
                title = "복원된 팬노트",
                subtitle = "복원된 부제목",
                content = "복원된 본문",
                productionDate = productionDate,
                coverImageUrl = "https://example.com/restored.jpg",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(fanNote.id).isEqualTo(id)
            assertThat(fanNote.id.value).isEqualTo(100L)
            assertThat(fanNote.title).isEqualTo("복원된 팬노트")
            assertThat(fanNote.subtitle).isEqualTo("복원된 부제목")
            assertThat(fanNote.content).isEqualTo("복원된 본문")
            assertThat(fanNote.productionDate).isEqualTo(productionDate)
            assertThat(fanNote.coverImageUrl).isEqualTo("https://example.com/restored.jpg")
            assertThat(fanNote.createdAt).isEqualTo(createdAt)
            assertThat(fanNote.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("선택 필드가 null인 데이터로 복원한다")
        fun `복원 - 선택 필드 null`() {
            val id = FanNoteId(200L)
            val productionDate = LocalDate.of(2024, 3, 10)
            val now = LocalDateTime.now()

            val fanNote = FanNote.reconstitute(
                id = id,
                title = "제목만 있는 팬노트",
                subtitle = null,
                content = null,
                productionDate = productionDate,
                coverImageUrl = null,
                createdAt = now,
                updatedAt = now
            )

            assertThat(fanNote.id).isEqualTo(id)
            assertThat(fanNote.title).isEqualTo("제목만 있는 팬노트")
            assertThat(fanNote.subtitle).isNull()
            assertThat(fanNote.content).isNull()
            assertThat(fanNote.coverImageUrl).isNull()
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 다른 시간으로 복원된다")
        fun `복원 - 시간 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 12, 30, 0)

            val fanNote = FanNote.reconstitute(
                id = FanNoteId(1L),
                title = "팬노트",
                subtitle = null,
                content = null,
                productionDate = LocalDate.of(2024, 1, 1),
                coverImageUrl = null,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(fanNote.createdAt).isEqualTo(createdAt)
            assertThat(fanNote.updatedAt).isEqualTo(updatedAt)
            assertThat(fanNote.updatedAt).isAfter(fanNote.createdAt)
        }
    }

    private fun createFanNote(): FanNote {
        return FanNote.create(
            title = "테스트 팬노트",
            subtitle = "테스트 부제목",
            content = "테스트 본문",
            productionDate = LocalDate.of(2024, 1, 15),
            coverImageUrl = "https://example.com/cover.jpg"
        )
    }

    private fun createReconstitutedFanNote(): FanNote {
        return FanNote.reconstitute(
            id = FanNoteId(1L),
            title = "테스트 팬노트",
            subtitle = "테스트 부제목",
            content = "테스트 본문",
            productionDate = LocalDate.of(2024, 1, 15),
            coverImageUrl = "https://example.com/cover.jpg",
            createdAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0),
            updatedAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0)
        )
    }
}
