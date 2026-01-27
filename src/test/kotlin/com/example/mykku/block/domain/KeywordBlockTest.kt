package com.example.mykku.block.domain

import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.vo.KeywordBlockId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("KeywordBlock 도메인 엔티티 테스트")
class KeywordBlockTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 키워드 차단을 생성한다")
        fun `키워드 차단 생성 - 정상 케이스`() {
            val memberId = "member-uuid"
            val keyword = "차단키워드"

            val keywordBlock = KeywordBlock.create(
                memberId = memberId,
                keyword = keyword
            )

            assertThat(keywordBlock.memberId).isEqualTo(memberId)
            assertThat(keywordBlock.keyword).isEqualTo(keyword)
        }

        @Test
        @DisplayName("생성시 id는 null이다")
        fun `키워드 차단 생성 - id null`() {
            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = "차단키워드"
            )

            assertThat(keywordBlock.id).isNull()
        }

        @Test
        @DisplayName("생성시 createdAt이 설정된다")
        fun `키워드 차단 생성 - createdAt 설정`() {
            val beforeCreate = LocalDateTime.now()

            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = "차단키워드"
            )

            assertThat(keywordBlock.createdAt).isNotNull()
            assertThat(keywordBlock.createdAt).isAfterOrEqualTo(beforeCreate)
        }

        @Test
        @DisplayName("생성시 updatedAt이 설정된다")
        fun `키워드 차단 생성 - updatedAt 설정`() {
            val beforeCreate = LocalDateTime.now()

            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = "차단키워드"
            )

            assertThat(keywordBlock.updatedAt).isNotNull()
            assertThat(keywordBlock.updatedAt).isAfterOrEqualTo(beforeCreate)
        }

        @Test
        @DisplayName("생성시 createdAt과 updatedAt이 동일하다")
        fun `키워드 차단 생성 - createdAt과 updatedAt 동일`() {
            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = "차단키워드"
            )

            assertThat(keywordBlock.createdAt).isEqualTo(keywordBlock.updatedAt)
        }

        @Test
        @DisplayName("키워드 길이가 정확히 50자일 때 성공한다")
        fun `키워드 차단 생성 - 길이 경계값 성공`() {
            val keyword = "a".repeat(KeywordBlock.KEYWORD_MAX_LENGTH)

            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = keyword
            )

            assertThat(keywordBlock.keyword.length).isEqualTo(KeywordBlock.KEYWORD_MAX_LENGTH)
        }

        @Test
        @DisplayName("키워드 길이가 50자를 초과하면 예외가 발생한다")
        fun `키워드 차단 생성 - 길이 초과 실패`() {
            val keyword = "a".repeat(KeywordBlock.KEYWORD_MAX_LENGTH + 1)

            val exception = assertThrows<IllegalArgumentException> {
                KeywordBlock.create(
                    memberId = "member-uuid",
                    keyword = keyword
                )
            }

            assertThat(exception.message).contains(KeywordBlock.KEYWORD_MAX_LENGTH.toString())
        }

        @Test
        @DisplayName("빈 키워드도 생성할 수 있다")
        fun `키워드 차단 생성 - 빈 키워드`() {
            val keywordBlock = KeywordBlock.create(
                memberId = "member-uuid",
                keyword = ""
            )

            assertThat(keywordBlock.keyword).isEmpty()
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 KeywordBlock을 복원한다")
        fun `복원 - 정상 케이스`() {
            val id = KeywordBlockId(1L)
            val memberId = "member-uuid"
            val keyword = "차단키워드"
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val keywordBlock = KeywordBlock.reconstitute(
                id = id,
                memberId = memberId,
                keyword = keyword,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(keywordBlock.id).isEqualTo(id)
            assertThat(keywordBlock.memberId).isEqualTo(memberId)
            assertThat(keywordBlock.keyword).isEqualTo(keyword)
            assertThat(keywordBlock.createdAt).isEqualTo(createdAt)
            assertThat(keywordBlock.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원시 id가 설정된다")
        fun `복원 - id 설정`() {
            val id = KeywordBlockId(100L)
            val now = LocalDateTime.now()

            val keywordBlock = KeywordBlock.reconstitute(
                id = id,
                memberId = "member-uuid",
                keyword = "차단키워드",
                createdAt = now,
                updatedAt = now
            )

            assertThat(keywordBlock.id).isNotNull()
            assertThat(keywordBlock.id!!.value).isEqualTo(100L)
        }

        @Test
        @DisplayName("복원시 키워드 길이 검증을 하지 않는다")
        fun `복원 - 길이 검증 없음`() {
            val longKeyword = "a".repeat(KeywordBlock.KEYWORD_MAX_LENGTH + 10)
            val now = LocalDateTime.now()

            val keywordBlock = KeywordBlock.reconstitute(
                id = KeywordBlockId(1L),
                memberId = "member-uuid",
                keyword = longKeyword,
                createdAt = now,
                updatedAt = now
            )

            assertThat(keywordBlock.keyword).isEqualTo(longKeyword)
        }
    }

    @Nested
    @DisplayName("상수 검증")
    inner class Constants {

        @Test
        @DisplayName("KEYWORD_MAX_LENGTH는 50이다")
        fun `상수 - KEYWORD_MAX_LENGTH`() {
            assertThat(KeywordBlock.KEYWORD_MAX_LENGTH).isEqualTo(50)
        }

        @Test
        @DisplayName("MAX_KEYWORD_COUNT는 100이다")
        fun `상수 - MAX_KEYWORD_COUNT`() {
            assertThat(KeywordBlock.MAX_KEYWORD_COUNT).isEqualTo(100)
        }
    }
}
