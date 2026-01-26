package com.example.mykku.scrap.domain

import com.example.mykku.scrap.domain.entity.FolderEntity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("FolderEntity 도메인 엔티티 테스트")
class FolderEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("description과 함께 폴더를 생성한다")
        fun `폴더 생성 - description 포함`() {
            val folder = FolderEntity.create(
                memberId = "member1",
                name = "테스트 폴더",
                description = "테스트 설명"
            )

            assertThat(folder.id).isNull()
            assertThat(folder.memberId).isEqualTo("member1")
            assertThat(folder.name).isEqualTo("테스트 폴더")
            assertThat(folder.description).isEqualTo("테스트 설명")
        }

        @Test
        @DisplayName("description 없이 폴더를 생성한다")
        fun `폴더 생성 - description 없음`() {
            val folder = FolderEntity.create(
                memberId = "member1",
                name = "테스트 폴더",
                description = null
            )

            assertThat(folder.id).isNull()
            assertThat(folder.memberId).isEqualTo("member1")
            assertThat(folder.name).isEqualTo("테스트 폴더")
            assertThat(folder.description).isNull()
        }

        @Test
        @DisplayName("폴더 생성시 createdAt과 updatedAt이 설정된다")
        fun `폴더 생성 - 시간 설정 검증`() {
            val folder = FolderEntity.create(
                memberId = "member1",
                name = "테스트 폴더",
                description = null
            )

            assertThat(folder.createdAt).isNotNull()
            assertThat(folder.updatedAt).isNotNull()
            assertThat(folder.createdAt).isEqualTo(folder.updatedAt)
        }
    }

    @Nested
    @DisplayName("updateInfo 메서드")
    inner class UpdateInfo {

        @Test
        @DisplayName("이름과 설명을 수정한다")
        fun `폴더 수정 - 이름과 설명 변경`() {
            val folder = createFolder()

            folder.updateInfo(newName = "새로운 이름", newDescription = "새로운 설명")

            assertThat(folder.name).isEqualTo("새로운 이름")
            assertThat(folder.description).isEqualTo("새로운 설명")
        }

        @Test
        @DisplayName("설명을 null로 변경할 수 있다")
        fun `폴더 수정 - 설명을 null로 변경`() {
            val folder = createFolder()

            folder.updateInfo(newName = "새로운 이름", newDescription = null)

            assertThat(folder.name).isEqualTo("새로운 이름")
            assertThat(folder.description).isNull()
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `폴더 수정 - 시간 갱신 검증`() {
            val folder = createFolder()
            val originalUpdatedAt = folder.updatedAt

            Thread.sleep(10)
            folder.updateInfo(newName = "새로운 이름", newDescription = null)

            assertThat(folder.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정시 기존 속성이 변경된다 (mutable)")
        fun `폴더 수정 - 동일 인스턴스 변경 확인`() {
            val folder = createFolder()
            val originalName = folder.name

            folder.updateInfo(newName = "새로운 이름", newDescription = "새로운 설명")

            assertThat(folder.name).isNotEqualTo(originalName)
            assertThat(folder.name).isEqualTo("새로운 이름")
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FolderEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0)

            val folder = FolderEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                name = "복원 폴더",
                description = "복원 설명",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(folder.id?.value).isEqualTo(1L)
            assertThat(folder.memberId).isEqualTo("member1")
            assertThat(folder.name).isEqualTo("복원 폴더")
            assertThat(folder.description).isEqualTo("복원 설명")
            assertThat(folder.createdAt).isEqualTo(createdAt)
            assertThat(folder.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("description이 null인 데이터를 복원한다")
        fun `복원 - description null`() {
            val now = LocalDateTime.now()

            val folder = FolderEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                name = "복원 폴더",
                description = null,
                createdAt = now,
                updatedAt = now
            )

            assertThat(folder.id?.value).isEqualTo(1L)
            assertThat(folder.description).isNull()
        }
    }

    private fun createFolder(): FolderEntity {
        return FolderEntity.create(
            memberId = "member1",
            name = "테스트 폴더",
            description = "테스트 설명"
        )
    }
}
