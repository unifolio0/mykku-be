package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.domain.entity.FolderEntity
import com.example.mykku.scrap.domain.vo.FolderId
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("FolderPersistenceAdapter 통합 테스트")
class FolderPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var folderPort: FolderPort

    private lateinit var savedMember: MemberJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
    }

    @Nested
    @DisplayName("save 메서드 - 새 폴더 생성")
    inner class SaveNewFolder {

        @Test
        @DisplayName("폴더를 저장하고 ID가 생성된다")
        fun `폴더 저장 - 정상 케이스`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "테스트 폴더",
                description = "테스트 설명"
            )

            val saved = folderPort.save(folder)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.name).isEqualTo("테스트 폴더")
            assertThat(saved.description).isEqualTo("테스트 설명")
        }

        @Test
        @DisplayName("description이 null인 폴더를 저장할 수 있다")
        fun `폴더 저장 - description null`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "테스트 폴더",
                description = null
            )

            val saved = folderPort.save(folder)

            assertThat(saved.description).isNull()
        }

        @Test
        @DisplayName("존재하지 않는 회원의 폴더를 저장하면 예외가 발생한다")
        fun `폴더 저장 - 존재하지 않는 회원`() {
            val folder = FolderEntity.create(
                memberId = "nonExistentMember",
                name = "테스트 폴더",
                description = null
            )

            assertThatThrownBy {
                folderPort.save(folder)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("save 메서드 - 폴더 수정")
    inner class SaveUpdateFolder {

        @Test
        @DisplayName("기존 폴더를 수정한다")
        fun `폴더 수정 - 정상 케이스`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "원래 이름",
                description = "원래 설명"
            )
            val saved = folderPort.save(folder)

            val updatedFolder = FolderEntity.reconstitute(
                id = saved.id!!.value,
                memberId = saved.memberId,
                name = "수정된 이름",
                description = "수정된 설명",
                createdAt = saved.createdAt,
                updatedAt = saved.updatedAt
            )
            val updated = folderPort.save(updatedFolder)

            assertThat(updated.id).isEqualTo(saved.id)
            assertThat(updated.name).isEqualTo("수정된 이름")
            assertThat(updated.description).isEqualTo("수정된 설명")
        }

        @Test
        @DisplayName("존재하지 않는 폴더를 수정하면 예외가 발생한다")
        fun `폴더 수정 - 존재하지 않는 폴더`() {
            val folder = FolderEntity.reconstitute(
                id = 999999L,
                memberId = savedMember.id,
                name = "테스트",
                description = null,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            assertThatThrownBy {
                folderPort.save(folder)
            }.isInstanceOf(ScrapException::class.java)
                .extracting("errorCode")
                .isEqualTo(ScrapErrorCode.FOLDER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndId 메서드")
    inner class FindByMemberIdAndId {

        @Test
        @DisplayName("회원 ID와 폴더 ID로 폴더를 조회한다")
        fun `폴더 조회 - 정상 케이스`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "테스트 폴더",
                description = null
            )
            val saved = folderPort.save(folder)

            val found = folderPort.findByMemberIdAndId(savedMember.id, saved.id!!.value)

            assertThat(found).isNotNull
            assertThat(found!!.id).isEqualTo(saved.id)
            assertThat(found.name).isEqualTo("테스트 폴더")
        }

        @Test
        @DisplayName("존재하지 않는 폴더 ID로 조회하면 null을 반환한다")
        fun `폴더 조회 - 존재하지 않는 폴더`() {
            val found = folderPort.findByMemberIdAndId(savedMember.id, 999999L)

            assertThat(found).isNull()
        }

        @Test
        @DisplayName("다른 회원의 폴더를 조회하면 null을 반환한다")
        fun `폴더 조회 - 다른 회원`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "테스트 폴더",
                description = null
            )
            val saved = folderPort.save(folder)

            val found = folderPort.findByMemberIdAndId("otherMember", saved.id!!.value)

            assertThat(found).isNull()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원의 모든 폴더를 조회한다")
        fun `폴더 목록 조회 - 정상 케이스`() {
            folderPort.save(FolderEntity.create(savedMember.id, "폴더1", null))
            folderPort.save(FolderEntity.create(savedMember.id, "폴더2", null))
            folderPort.save(FolderEntity.create(savedMember.id, "폴더3", null))

            val folders = folderPort.findByMemberId(savedMember.id)

            assertThat(folders).hasSize(3)
        }

        @Test
        @DisplayName("폴더가 없으면 빈 리스트를 반환한다")
        fun `폴더 목록 조회 - 폴더 없음`() {
            val folders = folderPort.findByMemberId(savedMember.id)

            assertThat(folders).isEmpty()
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndName 메서드")
    inner class ExistsByMemberIdAndName {

        @Test
        @DisplayName("같은 이름의 폴더가 존재하면 true를 반환한다")
        fun `폴더 이름 중복 확인 - 존재함`() {
            folderPort.save(FolderEntity.create(savedMember.id, "테스트 폴더", null))

            val exists = folderPort.existsByMemberIdAndName(savedMember.id, "테스트 폴더")

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("같은 이름의 폴더가 존재하지 않으면 false를 반환한다")
        fun `폴더 이름 중복 확인 - 존재하지 않음`() {
            val exists = folderPort.existsByMemberIdAndName(savedMember.id, "테스트 폴더")

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("다른 회원의 같은 이름 폴더는 중복으로 간주하지 않는다")
        fun `폴더 이름 중복 확인 - 다른 회원`() {
            val otherMember = createAndSaveMember(id = "otherMember", memberId = "otherMember", email = "other@example.com", socialId = "99999")
            folderPort.save(FolderEntity.create(otherMember.id, "테스트 폴더", null))

            val exists = folderPort.existsByMemberIdAndName(savedMember.id, "테스트 폴더")

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    inner class Delete {

        @Test
        @DisplayName("폴더를 삭제한다")
        fun `폴더 삭제 - 정상 케이스`() {
            val folder = FolderEntity.create(
                memberId = savedMember.id,
                name = "테스트 폴더",
                description = null
            )
            val saved = folderPort.save(folder)

            folderPort.delete(saved)

            val found = folderPort.findByMemberIdAndId(savedMember.id, saved.id!!.value)
            assertThat(found).isNull()
        }

        @Test
        @DisplayName("존재하지 않는 폴더를 삭제하면 예외가 발생한다")
        fun `폴더 삭제 - 존재하지 않는 폴더`() {
            val folder = FolderEntity.reconstitute(
                id = 999999L,
                memberId = savedMember.id,
                name = "테스트",
                description = null,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            assertThatThrownBy {
                folderPort.delete(folder)
            }.isInstanceOf(ScrapException::class.java)
                .extracting("errorCode")
                .isEqualTo(ScrapErrorCode.FOLDER_NOT_FOUND)
        }
    }
}
