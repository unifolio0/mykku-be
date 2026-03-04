package com.example.mykku.scrap.domain

import com.example.mykku.scrap.domain.entity.SaveContestEntity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveContestEntity 도메인 엔티티 테스트")
class SaveContestEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("콘테스트 스크랩을 생성한다")
        fun `콘테스트 스크랩 생성 - 정상 케이스`() {
            val saveContest = SaveContestEntity.create(
                memberId = 1L,
                contestId = 100L
            )

            assertThat(saveContest.id).isNull()
            assertThat(saveContest.memberId).isEqualTo(1L)
            assertThat(saveContest.contestId).isEqualTo(100L)
        }

        @Test
        @DisplayName("콘테스트 스크랩 생성시 createdAt과 updatedAt이 설정된다")
        fun `콘테스트 스크랩 생성 - 시간 설정 검증`() {
            val saveContest = SaveContestEntity.create(
                memberId = 1L,
                contestId = 100L
            )

            assertThat(saveContest.createdAt).isNotNull()
            assertThat(saveContest.updatedAt).isNotNull()
            assertThat(saveContest.createdAt).isEqualTo(saveContest.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 SaveContestEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0)

            val saveContest = SaveContestEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                contestId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveContest.id?.value).isEqualTo(1L)
            assertThat(saveContest.memberId).isEqualTo(1L)
            assertThat(saveContest.contestId).isEqualTo(100L)
            assertThat(saveContest.createdAt).isEqualTo(createdAt)
            assertThat(saveContest.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원시 모든 필드가 정확히 설정된다")
        fun `복원 - 필드 검증`() {
            val now = LocalDateTime.now()

            val saveContest = SaveContestEntity.reconstitute(
                id = 999L,
                memberId = 2L,
                contestId = 12345L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(saveContest.id?.value).isEqualTo(999L)
            assertThat(saveContest.memberId).isEqualTo(2L)
            assertThat(saveContest.contestId).isEqualTo(12345L)
        }
    }
}
