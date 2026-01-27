package com.example.mykku.dailymessage.domain

import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageErrorCode
import com.example.mykku.dailymessage.exception.DailyMessageException
import java.time.LocalDate
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("DailyMessage 도메인 엔티티 테스트")
class DailyMessageTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 일상 메시지를 생성한다")
        fun `일상 메시지 생성 - 정상 케이스`() {
            val dailyMessage = DailyMessage.create(
                title = "테스트 제목",
                content = "테스트 내용",
                date = LocalDate.of(2025, 1, 26)
            )

            assertThat(dailyMessage.id.value).isEqualTo(0L)
            assertThat(dailyMessage.title).isEqualTo("테스트 제목")
            assertThat(dailyMessage.content).isEqualTo("테스트 내용")
            assertThat(dailyMessage.date).isEqualTo(LocalDate.of(2025, 1, 26))
        }

        @Test
        @DisplayName("일상 메시지 생성시 createdAt과 updatedAt이 설정된다")
        fun `일상 메시지 생성 - 시간 설정 검증`() {
            val dailyMessage = DailyMessage.create(
                title = "테스트 제목",
                content = "테스트 내용",
                date = LocalDate.now()
            )

            assertThat(dailyMessage.createdAt).isNotNull()
            assertThat(dailyMessage.updatedAt).isNotNull()
            assertThat(dailyMessage.createdAt).isEqualTo(dailyMessage.updatedAt)
        }

        @Test
        @DisplayName("컨텐츠가 42자를 초과하면 예외가 발생한다")
        fun `일상 메시지 생성 - 컨텐츠 길이 초과시 예외`() {
            val longContent = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<DailyMessageException> {
                DailyMessage.create(
                    title = "테스트 제목",
                    content = longContent,
                    date = LocalDate.now()
                )
            }

            assertThat(exception.errorCode).isEqualTo(DailyMessageErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG)
        }

        @Test
        @DisplayName("컨텐츠가 정확히 42자일 때는 생성된다")
        fun `일상 메시지 생성 - 컨텐츠 길이 경계값`() {
            val exactContent = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH)

            val dailyMessage = DailyMessage.create(
                title = "테스트 제목",
                content = exactContent,
                date = LocalDate.now()
            )

            assertThat(dailyMessage.content.length).isEqualTo(DailyMessage.CONTENT_MAX_LENGTH)
        }

        @Test
        @DisplayName("빈 컨텐츠로도 생성할 수 있다")
        fun `일상 메시지 생성 - 빈 컨텐츠`() {
            val dailyMessage = DailyMessage.create(
                title = "테스트 제목",
                content = "",
                date = LocalDate.now()
            )

            assertThat(dailyMessage.content).isEmpty()
        }
    }

    @Nested
    @DisplayName("update 메서드")
    inner class Update {

        @Test
        @DisplayName("일상 메시지를 수정할 수 있다")
        fun `일상 메시지 수정 - 정상 케이스`() {
            val dailyMessage = createDailyMessage()

            val updatedMessage = dailyMessage.update(
                title = "새로운 제목",
                content = "새로운 내용",
                date = LocalDate.of(2025, 2, 1)
            )

            assertThat(updatedMessage.title).isEqualTo("새로운 제목")
            assertThat(updatedMessage.content).isEqualTo("새로운 내용")
            assertThat(updatedMessage.date).isEqualTo(LocalDate.of(2025, 2, 1))
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `일상 메시지 수정 - 시간 갱신 검증`() {
            val dailyMessage = createDailyMessage()
            val originalUpdatedAt = dailyMessage.updatedAt

            Thread.sleep(10)
            val updatedMessage = dailyMessage.update(
                title = "새로운 제목",
                content = "새로운 내용",
                date = LocalDate.now()
            )

            assertThat(updatedMessage.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정시 id와 createdAt은 유지된다")
        fun `일상 메시지 수정 - 불변 필드 유지`() {
            val now = LocalDateTime.now()
            val dailyMessage = DailyMessage.reconstitute(
                id = DailyMessageId(1L),
                title = "원본 제목",
                content = "원본 내용",
                date = LocalDate.of(2025, 1, 1),
                createdAt = now,
                updatedAt = now
            )

            val updatedMessage = dailyMessage.update(
                title = "새로운 제목",
                content = "새로운 내용",
                date = LocalDate.of(2025, 2, 1)
            )

            assertThat(updatedMessage.id.value).isEqualTo(1L)
            assertThat(updatedMessage.createdAt).isEqualTo(now)
        }

        @Test
        @DisplayName("수정할 내용이 42자를 초과하면 예외가 발생한다")
        fun `일상 메시지 수정 - 컨텐츠 길이 초과시 예외`() {
            val dailyMessage = createDailyMessage()
            val longContent = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<DailyMessageException> {
                dailyMessage.update(
                    title = "새로운 제목",
                    content = longContent,
                    date = LocalDate.now()
                )
            }

            assertThat(exception.errorCode).isEqualTo(DailyMessageErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG)
        }

        @Test
        @DisplayName("수정할 내용이 정확히 42자일 때는 수정된다")
        fun `일상 메시지 수정 - 컨텐츠 길이 경계값`() {
            val dailyMessage = createDailyMessage()
            val exactContent = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH)

            val updatedMessage = dailyMessage.update(
                title = "새로운 제목",
                content = exactContent,
                date = LocalDate.now()
            )

            assertThat(updatedMessage.content.length).isEqualTo(DailyMessage.CONTENT_MAX_LENGTH)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 DailyMessage를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val date = LocalDate.of(2025, 1, 26)
            val dailyMessage = DailyMessage.reconstitute(
                id = DailyMessageId(1L),
                title = "제목",
                content = "내용",
                date = date,
                createdAt = now,
                updatedAt = now
            )

            assertThat(dailyMessage.id.value).isEqualTo(1L)
            assertThat(dailyMessage.title).isEqualTo("제목")
            assertThat(dailyMessage.content).isEqualTo("내용")
            assertThat(dailyMessage.date).isEqualTo(date)
            assertThat(dailyMessage.createdAt).isEqualTo(now)
            assertThat(dailyMessage.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("복원시 검증을 수행하지 않는다")
        fun `복원 - 검증 미수행`() {
            val now = LocalDateTime.now()
            val longContent = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH + 100)

            val dailyMessage = DailyMessage.reconstitute(
                id = DailyMessageId(1L),
                title = "제목",
                content = longContent,
                date = LocalDate.now(),
                createdAt = now,
                updatedAt = now
            )

            assertThat(dailyMessage.content.length).isGreaterThan(DailyMessage.CONTENT_MAX_LENGTH)
        }
    }

    @Nested
    @DisplayName("CONTENT_MAX_LENGTH 상수")
    inner class ContentMaxLength {

        @Test
        @DisplayName("CONTENT_MAX_LENGTH는 42이다")
        fun `상수값 검증`() {
            assertThat(DailyMessage.CONTENT_MAX_LENGTH).isEqualTo(42)
        }
    }

    private fun createDailyMessage(): DailyMessage {
        return DailyMessage.create(
            title = "테스트 제목",
            content = "테스트 내용",
            date = LocalDate.of(2025, 1, 26)
        )
    }
}
