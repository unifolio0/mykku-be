package com.example.mykku.contest.domain

import com.example.mykku.contest.domain.entity.ContestWinner
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import com.example.mykku.contest.domain.vo.ContestWinnerId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("ContestWinner 도메인 엔티티 테스트")
class ContestWinnerTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 콘테스트 수상자를 생성한다")
        fun `콘테스트 수상자 생성 - 정상 케이스`() {
            val winnerRank = 1
            val description = "1등 수상자"
            val contestId = ContestId(10L)
            val participationId = ContestParticipationId(100L)

            val winner = ContestWinner.create(
                winnerRank = winnerRank,
                description = description,
                contestId = contestId,
                participationId = participationId
            )

            assertThat(winner.winnerRank).isEqualTo(winnerRank)
            assertThat(winner.description).isEqualTo(description)
            assertThat(winner.contestId).isEqualTo(contestId)
            assertThat(winner.participationId).isEqualTo(participationId)
        }

        @Test
        @DisplayName("콘테스트 수상자 생성시 id가 0이다")
        fun `콘테스트 수상자 생성 - id 초기값`() {
            val winner = createWinner()

            assertThat(winner.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("콘테스트 수상자 생성시 acceptanceSpeech가 빈 문자열이다")
        fun `콘테스트 수상자 생성 - acceptanceSpeech 초기값`() {
            val winner = createWinner()

            assertThat(winner.acceptanceSpeech).isEmpty()
        }

        @Test
        @DisplayName("콘테스트 수상자 생성시 createdAt과 updatedAt이 설정된다")
        fun `콘테스트 수상자 생성 - 시간 설정 검증`() {
            val winner = createWinner()

            assertThat(winner.createdAt).isNotNull()
            assertThat(winner.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("콘테스트 수상자 생성시 createdAt과 updatedAt이 동일하다")
        fun `콘테스트 수상자 생성 - 시간 동일 검증`() {
            val winner = createWinner()

            assertThat(winner.createdAt).isEqualTo(winner.updatedAt)
        }

        @Test
        @DisplayName("다양한 순위로 수상자를 생성할 수 있다")
        fun `콘테스트 수상자 생성 - 다양한 순위`() {
            val firstPlace = ContestWinner.create(
                winnerRank = 1,
                description = "1등",
                contestId = ContestId(10L),
                participationId = ContestParticipationId(100L)
            )

            val secondPlace = ContestWinner.create(
                winnerRank = 2,
                description = "2등",
                contestId = ContestId(10L),
                participationId = ContestParticipationId(101L)
            )

            val thirdPlace = ContestWinner.create(
                winnerRank = 3,
                description = "3등",
                contestId = ContestId(10L),
                participationId = ContestParticipationId(102L)
            )

            assertThat(firstPlace.winnerRank).isEqualTo(1)
            assertThat(secondPlace.winnerRank).isEqualTo(2)
            assertThat(thirdPlace.winnerRank).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("updateAcceptanceSpeech 메서드")
    inner class UpdateAcceptanceSpeech {

        @Test
        @DisplayName("수상 소감을 업데이트할 수 있다")
        fun `수상 소감 업데이트 - 정상 케이스`() {
            val winner = createWinner()
            val speech = "감사합니다! 열심히 준비한 보람이 있네요."

            winner.updateAcceptanceSpeech(speech)

            assertThat(winner.acceptanceSpeech).isEqualTo(speech)
        }

        @Test
        @DisplayName("수상 소감을 빈 문자열로 업데이트할 수 있다")
        fun `수상 소감 업데이트 - 빈 문자열`() {
            val winner = createWinner()
            winner.updateAcceptanceSpeech("초기 소감")

            winner.updateAcceptanceSpeech("")

            assertThat(winner.acceptanceSpeech).isEmpty()
        }

        @Test
        @DisplayName("수상 소감을 여러 번 업데이트할 수 있다")
        fun `수상 소감 업데이트 - 여러 번`() {
            val winner = createWinner()

            winner.updateAcceptanceSpeech("첫 번째 소감")
            assertThat(winner.acceptanceSpeech).isEqualTo("첫 번째 소감")

            winner.updateAcceptanceSpeech("두 번째 소감")
            assertThat(winner.acceptanceSpeech).isEqualTo("두 번째 소감")

            winner.updateAcceptanceSpeech("최종 소감")
            assertThat(winner.acceptanceSpeech).isEqualTo("최종 소감")
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 ContestWinner를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = ContestWinnerId(1L)
            val winnerRank = 1
            val description = "1등 수상자"
            val acceptanceSpeech = "감사합니다"
            val contestId = ContestId(10L)
            val participationId = ContestParticipationId(100L)

            val winner = ContestWinner.reconstitute(
                id = id,
                winnerRank = winnerRank,
                description = description,
                acceptanceSpeech = acceptanceSpeech,
                contestId = contestId,
                participationId = participationId,
                createdAt = now,
                updatedAt = now
            )

            assertThat(winner.id).isEqualTo(id)
            assertThat(winner.winnerRank).isEqualTo(winnerRank)
            assertThat(winner.description).isEqualTo(description)
            assertThat(winner.acceptanceSpeech).isEqualTo(acceptanceSpeech)
            assertThat(winner.contestId).isEqualTo(contestId)
            assertThat(winner.participationId).isEqualTo(participationId)
            assertThat(winner.createdAt).isEqualTo(now)
            assertThat(winner.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30)

            val winner = ContestWinner.reconstitute(
                id = ContestWinnerId(1L),
                winnerRank = 1,
                description = "설명",
                acceptanceSpeech = "소감",
                contestId = ContestId(10L),
                participationId = ContestParticipationId(100L),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(winner.createdAt).isEqualTo(createdAt)
            assertThat(winner.updatedAt).isEqualTo(updatedAt)
            assertThat(winner.createdAt).isNotEqualTo(winner.updatedAt)
        }

        @Test
        @DisplayName("수상 소감이 포함된 상태로 복원할 수 있다")
        fun `복원 - 수상 소감 포함`() {
            val now = LocalDateTime.now()
            val speech = "정말 감사드립니다. 앞으로도 열심히 하겠습니다!"

            val winner = ContestWinner.reconstitute(
                id = ContestWinnerId(1L),
                winnerRank = 1,
                description = "1등",
                acceptanceSpeech = speech,
                contestId = ContestId(10L),
                participationId = ContestParticipationId(100L),
                createdAt = now,
                updatedAt = now
            )

            assertThat(winner.acceptanceSpeech).isEqualTo(speech)
        }

        @Test
        @DisplayName("복원 후에도 수상 소감을 업데이트할 수 있다")
        fun `복원 - 이후 업데이트`() {
            val now = LocalDateTime.now()

            val winner = ContestWinner.reconstitute(
                id = ContestWinnerId(1L),
                winnerRank = 1,
                description = "1등",
                acceptanceSpeech = "기존 소감",
                contestId = ContestId(10L),
                participationId = ContestParticipationId(100L),
                createdAt = now,
                updatedAt = now
            )

            winner.updateAcceptanceSpeech("새로운 소감")

            assertThat(winner.acceptanceSpeech).isEqualTo("새로운 소감")
        }
    }

    private fun createWinner(): ContestWinner {
        return ContestWinner.create(
            winnerRank = 1,
            description = "1등 수상자",
            contestId = ContestId(10L),
            participationId = ContestParticipationId(100L)
        )
    }
}
