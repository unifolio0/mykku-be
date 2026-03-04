package com.example.mykku.scrap.domain

import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveDailyMessageEntity 도메인 엔티티 테스트")
class SaveDailyMessageEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("데일리 메시지 스크랩을 생성한다")
        fun `데일리 메시지 스크랩 생성 - 정상 케이스`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = 1L,
                dailyMessageId = 1L
            )

            assertThat(saveDailyMessage.id).isNull()
            assertThat(saveDailyMessage.memberId).isEqualTo(1L)
            assertThat(saveDailyMessage.dailyMessageId).isEqualTo(1L)
        }

        @Test
        @DisplayName("데일리 메시지 스크랩 생성시 createdAt과 updatedAt이 설정된다")
        fun `데일리 메시지 스크랩 생성 - 시간 설정 검증`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = 1L,
                dailyMessageId = 1L
            )

            assertThat(saveDailyMessage.createdAt).isNotNull()
            assertThat(saveDailyMessage.updatedAt).isNotNull()
            assertThat(saveDailyMessage.createdAt).isEqualTo(saveDailyMessage.updatedAt)
        }

        @Test
        @DisplayName("다른 memberId와 dailyMessageId로 생성할 수 있다")
        fun `데일리 메시지 스크랩 생성 - 다양한 값`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = 2L,
                dailyMessageId = 999L
            )

            assertThat(saveDailyMessage.memberId).isEqualTo(2L)
            assertThat(saveDailyMessage.dailyMessageId).isEqualTo(999L)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 SaveDailyMessageEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0)

            val saveDailyMessage = SaveDailyMessageEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                dailyMessageId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveDailyMessage.id?.value).isEqualTo(1L)
            assertThat(saveDailyMessage.memberId).isEqualTo(1L)
            assertThat(saveDailyMessage.dailyMessageId).isEqualTo(100L)
            assertThat(saveDailyMessage.createdAt).isEqualTo(createdAt)
            assertThat(saveDailyMessage.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("서로 다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간으로 복원`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 15, 30)

            val saveDailyMessage = SaveDailyMessageEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                dailyMessageId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveDailyMessage.createdAt).isNotEqualTo(saveDailyMessage.updatedAt)
            assertThat(saveDailyMessage.updatedAt).isAfter(saveDailyMessage.createdAt)
        }

        @Test
        @DisplayName("같은 시간으로 복원할 수 있다")
        fun `복원 - 같은 시간으로 복원`() {
            val now = LocalDateTime.now()

            val saveDailyMessage = SaveDailyMessageEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                dailyMessageId = 100L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(saveDailyMessage.createdAt).isEqualTo(saveDailyMessage.updatedAt)
        }
    }
}
