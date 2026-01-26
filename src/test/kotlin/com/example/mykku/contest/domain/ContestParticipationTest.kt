package com.example.mykku.contest.domain

import com.example.mykku.contest.domain.entity.ContestParticipation
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("ContestParticipation 도메인 엔티티 테스트")
class ContestParticipationTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 콘테스트 참여를 생성한다")
        fun `콘테스트 참여 생성 - 정상 케이스`() {
            val contestId = ContestId(1L)
            val feedId = 100L
            val memberId = 200L

            val participation = ContestParticipation.create(
                contestId = contestId,
                feedId = feedId,
                memberId = memberId
            )

            assertThat(participation.contestId).isEqualTo(contestId)
            assertThat(participation.feedId).isEqualTo(feedId)
            assertThat(participation.memberId).isEqualTo(memberId)
        }

        @Test
        @DisplayName("콘테스트 참여 생성시 id가 0이다")
        fun `콘테스트 참여 생성 - id 초기값`() {
            val participation = createParticipation()

            assertThat(participation.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("콘테스트 참여 생성시 createdAt과 updatedAt이 설정된다")
        fun `콘테스트 참여 생성 - 시간 설정 검증`() {
            val participation = createParticipation()

            assertThat(participation.createdAt).isNotNull()
            assertThat(participation.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("콘테스트 참여 생성시 createdAt과 updatedAt이 동일하다")
        fun `콘테스트 참여 생성 - 시간 동일 검증`() {
            val participation = createParticipation()

            assertThat(participation.createdAt).isEqualTo(participation.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 ContestParticipation을 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = ContestParticipationId(1L)
            val contestId = ContestId(10L)
            val feedId = 100L
            val memberId = 200L

            val participation = ContestParticipation.reconstitute(
                id = id,
                contestId = contestId,
                feedId = feedId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )

            assertThat(participation.id).isEqualTo(id)
            assertThat(participation.contestId).isEqualTo(contestId)
            assertThat(participation.feedId).isEqualTo(feedId)
            assertThat(participation.memberId).isEqualTo(memberId)
            assertThat(participation.createdAt).isEqualTo(now)
            assertThat(participation.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30)

            val participation = ContestParticipation.reconstitute(
                id = ContestParticipationId(1L),
                contestId = ContestId(10L),
                feedId = 100L,
                memberId = 200L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(participation.createdAt).isEqualTo(createdAt)
            assertThat(participation.updatedAt).isEqualTo(updatedAt)
            assertThat(participation.createdAt).isNotEqualTo(participation.updatedAt)
        }

        @Test
        @DisplayName("특정 id로 복원할 수 있다")
        fun `복원 - 특정 id`() {
            val specificId = ContestParticipationId(999L)
            val now = LocalDateTime.now()

            val participation = ContestParticipation.reconstitute(
                id = specificId,
                contestId = ContestId(10L),
                feedId = 100L,
                memberId = 200L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(participation.id.value).isEqualTo(999L)
        }
    }

    private fun createParticipation(): ContestParticipation {
        return ContestParticipation.create(
            contestId = ContestId(1L),
            feedId = 100L,
            memberId = 200L
        )
    }
}
