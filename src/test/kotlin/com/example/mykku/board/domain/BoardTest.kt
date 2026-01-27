package com.example.mykku.board.domain

import com.example.mykku.board.domain.entity.Board
import com.example.mykku.board.domain.vo.BoardId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Board 도메인 엔티티 테스트")
class BoardTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 Board를 생성한다")
        fun `Board 생성 - 정상 케이스`() {
            val board = Board.create(
                title = "테스트 게시판",
                logo = "https://example.com/logo.png"
            )

            assertThat(board.title).isEqualTo("테스트 게시판")
            assertThat(board.logo).isEqualTo("https://example.com/logo.png")
        }

        @Test
        @DisplayName("생성 시 id는 BoardId(0L)이다")
        fun `Board 생성 - id 초기값`() {
            val board = Board.create(
                title = "테스트 게시판",
                logo = "https://example.com/logo.png"
            )

            assertThat(board.id).isEqualTo(BoardId(0L))
        }

        @Test
        @DisplayName("생성 시 createdAt과 updatedAt이 설정된다")
        fun `Board 생성 - 시간 설정 검증`() {
            val board = Board.create(
                title = "테스트 게시판",
                logo = "https://example.com/logo.png"
            )

            assertThat(board.createdAt).isNotNull()
            assertThat(board.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("생성 시 createdAt과 updatedAt이 동일하다")
        fun `Board 생성 - 시간 동일 검증`() {
            val board = Board.create(
                title = "테스트 게시판",
                logo = "https://example.com/logo.png"
            )

            assertThat(board.createdAt).isEqualTo(board.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Board를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val board = Board.reconstitute(
                id = BoardId(1L),
                title = "복원된 게시판",
                logo = "https://example.com/restored-logo.png",
                createdAt = now,
                updatedAt = now
            )

            assertThat(board.id).isEqualTo(BoardId(1L))
            assertThat(board.title).isEqualTo("복원된 게시판")
            assertThat(board.logo).isEqualTo("https://example.com/restored-logo.png")
            assertThat(board.createdAt).isEqualTo(now)
            assertThat(board.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("복원 시 createdAt과 updatedAt이 다를 수 있다")
        fun `복원 - 시간 차이 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 12, 30, 0)

            val board = Board.reconstitute(
                id = BoardId(1L),
                title = "복원된 게시판",
                logo = "https://example.com/logo.png",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(board.createdAt).isEqualTo(createdAt)
            assertThat(board.updatedAt).isEqualTo(updatedAt)
            assertThat(board.updatedAt).isAfter(board.createdAt)
        }
    }
}
