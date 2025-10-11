package com.example.mykku.scrap.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.scrap.domain.Folder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DisplayName("FolderRepository 테스트")
class FolderRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var folderRepository: FolderRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("폴더를 저장하고 조회한다")
    fun `폴더를 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val folder = Folder(
            member = member,
            name = "내가 좋아하는 피드",
            description = "좋아하는 게시물 모음"
        )

        // when
        val savedFolder = folderRepository.save(folder)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundFolder = folderRepository.findById(savedFolder.id!!).orElse(null)

        // then
        assertThat(foundFolder).isNotNull
        assertThat(foundFolder.name).isEqualTo("내가 좋아하는 피드")
        assertThat(foundFolder.description).isEqualTo("좋아하는 게시물 모음")
        assertThat(foundFolder.member.id).isEqualTo(member.id)
    }

    @Test
    @DisplayName("회원과 폴더 ID로 폴더를 조회한다")
    fun `회원과 폴더 ID로 폴더를 조회한다`() {
        // given
        val member = createAndSaveMember()
        val folder = Folder(
            member = member,
            name = "테스트 폴더",
            description = null
        )
        val savedFolder = folderRepository.save(folder)
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundFolder = folderRepository.findByMemberAndId(member, savedFolder.id!!)

        // then
        assertThat(foundFolder).isNotNull
        assertThat(foundFolder!!.name).isEqualTo("테스트 폴더")
    }

    @Test
    @DisplayName("다른 회원의 폴더는 조회되지 않는다")
    fun `다른 회원의 폴더는 조회되지 않는다`() {
        // given
        val member1 = createAndSaveMember(id = "member1")
        val member2 = createAndSaveMember(id = "member2")

        val folder = Folder(
            member = member1,
            name = "member1의 폴더",
            description = null
        )
        val savedFolder = folderRepository.save(folder)
        testEntityManager.flush()

        // when
        val foundFolder = folderRepository.findByMemberAndId(member2, savedFolder.id!!)

        // then
        assertThat(foundFolder).isNull()
    }

    @Test
    @DisplayName("회원의 모든 폴더를 조회한다")
    fun `회원의 모든 폴더를 조회한다`() {
        // given
        val member = createAndSaveMember()
        val folder1 = Folder(member = member, name = "폴더1", description = null)
        val folder2 = Folder(member = member, name = "폴더2", description = "설명2")
        val folder3 = Folder(member = member, name = "폴더3", description = null)

        folderRepository.saveAll(listOf(folder1, folder2, folder3))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val folders = folderRepository.findByMember(member)

        // then
        assertThat(folders).hasSize(3)
        assertThat(folders.map { it.name }).containsExactlyInAnyOrder("폴더1", "폴더2", "폴더3")
    }

    @Test
    @DisplayName("회원과 폴더 이름으로 중복 체크를 한다")
    fun `회원과 폴더 이름으로 중복 체크를 한다`() {
        // given
        val member = createAndSaveMember()
        val folder = Folder(member = member, name = "중복 테스트", description = null)
        folderRepository.save(folder)
        testEntityManager.flush()

        // when
        val exists = folderRepository.existsByMemberAndName(member, "중복 테스트")
        val notExists = folderRepository.existsByMemberAndName(member, "존재하지 않는 폴더")

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    @DisplayName("다른 회원은 같은 이름의 폴더를 가질 수 있다")
    fun `다른 회원은 같은 이름의 폴더를 가질 수 있다`() {
        // given
        val member1 = createAndSaveMember(id = "member1")
        val member2 = createAndSaveMember(id = "member2")

        val folder1 = Folder(member = member1, name = "같은 이름", description = null)
        val folder2 = Folder(member = member2, name = "같은 이름", description = null)

        // when
        folderRepository.save(folder1)
        folderRepository.save(folder2)
        testEntityManager.flush()

        // then
        val member1Exists = folderRepository.existsByMemberAndName(member1, "같은 이름")
        val member2Exists = folderRepository.existsByMemberAndName(member2, "같은 이름")

        assertThat(member1Exists).isTrue()
        assertThat(member2Exists).isTrue()
    }

    @Test
    @DisplayName("폴더를 수정한다")
    fun `폴더를 수정한다`() {
        // given
        val member = createAndSaveMember()
        val folder = Folder(member = member, name = "원래 이름", description = "원래 설명")
        val savedFolder = folderRepository.save(folder)
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundFolder = folderRepository.findById(savedFolder.id!!).orElseThrow()
        foundFolder.updateInfo("변경된 이름", "변경된 설명")
        folderRepository.save(foundFolder)
        testEntityManager.flush()
        testEntityManager.clear()

        // then
        val updatedFolder = folderRepository.findById(savedFolder.id!!).orElseThrow()
        assertThat(updatedFolder.name).isEqualTo("변경된 이름")
        assertThat(updatedFolder.description).isEqualTo("변경된 설명")
    }

    @Test
    @DisplayName("폴더를 삭제한다")
    fun `폴더를 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val folder = Folder(member = member, name = "삭제할 폴더", description = null)
        val savedFolder = folderRepository.save(folder)
        testEntityManager.flush()

        // when
        folderRepository.deleteById(savedFolder.id!!)
        testEntityManager.flush()

        // then
        val foundFolder = folderRepository.findById(savedFolder.id!!)
        assertThat(foundFolder).isEmpty
    }
}
