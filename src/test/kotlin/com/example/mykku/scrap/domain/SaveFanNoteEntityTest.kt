package com.example.mykku.scrap.domain

import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveFanNoteEntity 도메인 엔티티 테스트")
class SaveFanNoteEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("팬노트 스크랩을 생성한다")
        fun `팬노트 스크랩 생성 - 정상 케이스`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = "member1",
                fanNoteId = 100L
            )

            assertThat(saveFanNote.id).isNull()
            assertThat(saveFanNote.memberId).isEqualTo("member1")
            assertThat(saveFanNote.fanNoteId).isEqualTo(100L)
        }

        @Test
        @DisplayName("팬노트 스크랩 생성시 createdAt과 updatedAt이 설정된다")
        fun `팬노트 스크랩 생성 - 시간 설정 검증`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = "member1",
                fanNoteId = 100L
            )

            assertThat(saveFanNote.createdAt).isNotNull()
            assertThat(saveFanNote.updatedAt).isNotNull()
            assertThat(saveFanNote.createdAt).isEqualTo(saveFanNote.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 SaveFanNoteEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0)

            val saveFanNote = SaveFanNoteEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                fanNoteId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveFanNote.id?.value).isEqualTo(1L)
            assertThat(saveFanNote.memberId).isEqualTo("member1")
            assertThat(saveFanNote.fanNoteId).isEqualTo(100L)
            assertThat(saveFanNote.createdAt).isEqualTo(createdAt)
            assertThat(saveFanNote.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원시 모든 필드가 정확히 설정된다")
        fun `복원 - 필드 검증`() {
            val now = LocalDateTime.now()

            val saveFanNote = SaveFanNoteEntity.reconstitute(
                id = 999L,
                memberId = "test-member",
                fanNoteId = 12345L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(saveFanNote.id?.value).isEqualTo(999L)
            assertThat(saveFanNote.memberId).isEqualTo("test-member")
            assertThat(saveFanNote.fanNoteId).isEqualTo(12345L)
        }
    }
}
