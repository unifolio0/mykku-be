package com.example.mykku.scrap.domain

import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveFeedEntity 도메인 엔티티 테스트")
class SaveFeedEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("피드 스크랩을 생성한다")
        fun `피드 스크랩 생성 - 정상 케이스`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = 1L,
                feedId = 1L,
                folderId = 10L
            )

            assertThat(saveFeed.id).isNull()
            assertThat(saveFeed.memberId).isEqualTo(1L)
            assertThat(saveFeed.feedId).isEqualTo(1L)
            assertThat(saveFeed.folderId).isEqualTo(10L)
        }

        @Test
        @DisplayName("피드 스크랩 생성시 createdAt과 updatedAt이 설정된다")
        fun `피드 스크랩 생성 - 시간 설정 검증`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = 1L,
                feedId = 1L,
                folderId = 10L
            )

            assertThat(saveFeed.createdAt).isNotNull()
            assertThat(saveFeed.updatedAt).isNotNull()
            assertThat(saveFeed.createdAt).isEqualTo(saveFeed.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 SaveFeedEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0)

            val saveFeed = SaveFeedEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                feedId = 100L,
                folderId = 10L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveFeed.id?.value).isEqualTo(1L)
            assertThat(saveFeed.memberId).isEqualTo(1L)
            assertThat(saveFeed.feedId).isEqualTo(100L)
            assertThat(saveFeed.folderId).isEqualTo(10L)
            assertThat(saveFeed.createdAt).isEqualTo(createdAt)
            assertThat(saveFeed.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("서로 다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간으로 복원`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 15, 30)

            val saveFeed = SaveFeedEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                feedId = 100L,
                folderId = 10L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(saveFeed.createdAt).isNotEqualTo(saveFeed.updatedAt)
            assertThat(saveFeed.updatedAt).isAfter(saveFeed.createdAt)
        }
    }

    @Nested
    @DisplayName("updateFolder 메서드")
    inner class UpdateFolder {

        @Test
        @DisplayName("폴더를 변경한다")
        fun `폴더 변경 - 정상 케이스`() {
            val saveFeed = createSaveFeed()

            saveFeed.updateFolder(newFolderId = 20L)

            assertThat(saveFeed.folderId).isEqualTo(20L)
        }

        @Test
        @DisplayName("폴더 변경시 updatedAt이 갱신된다")
        fun `폴더 변경 - 시간 갱신 검증`() {
            val saveFeed = createSaveFeed()
            val originalUpdatedAt = saveFeed.updatedAt

            Thread.sleep(10)
            saveFeed.updateFolder(newFolderId = 20L)

            assertThat(saveFeed.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("폴더 변경시 기존 폴더 ID가 변경된다 (mutable)")
        fun `폴더 변경 - 동일 인스턴스 변경 확인`() {
            val saveFeed = createSaveFeed()
            val originalFolderId = saveFeed.folderId

            saveFeed.updateFolder(newFolderId = 20L)

            assertThat(saveFeed.folderId).isNotEqualTo(originalFolderId)
            assertThat(saveFeed.folderId).isEqualTo(20L)
        }
    }

    private fun createSaveFeed(): SaveFeedEntity {
        return SaveFeedEntity.create(
            memberId = 1L,
            feedId = 1L,
            folderId = 10L
        )
    }
}
