package com.example.mykku.scrap.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.repository.FanNoteRepository
import com.example.mykku.scrap.domain.SaveFanNote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

@DisplayName("SaveFanNoteRepository 테스트")
class SaveFanNoteRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveFanNoteRepository: SaveFanNoteRepository

    @Autowired
    private lateinit var fanNoteRepository: FanNoteRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("덕질노트를 저장하고 조회한다")
    fun `덕질노트를 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val fanNote = fanNoteRepository.save(
            FanNote(
                title = "테스트 덕질노트",
                subtitle = "서브타이틀",
                content = "내용",
                productionDate = LocalDate.now(),
                coverImageUrl = "https://example.com/cover.jpg"
            )
        )

        val saveFanNote = SaveFanNote(
            member = member,
            fanNote = fanNote
        )

        // when
        val saved = saveFanNoteRepository.save(saveFanNote)
        testEntityManager.flush()
        testEntityManager.clear()

        val found = saveFanNoteRepository.findById(saved.id!!).orElse(null)

        // then
        assertThat(found).isNotNull
        assertThat(found.member.id).isEqualTo(member.id)
        assertThat(found.fanNote.id).isEqualTo(fanNote.id)
    }

    @Test
    @DisplayName("회원과 덕질노트로 저장 여부를 확인한다")
    fun `회원과 덕질노트로 저장 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val fanNote = fanNoteRepository.save(
            FanNote(
                title = "덕질노트",
                subtitle = null,
                content = null,
                productionDate = LocalDate.now(),
                coverImageUrl = null
            )
        )

        saveFanNoteRepository.save(SaveFanNote(member = member, fanNote = fanNote))
        testEntityManager.flush()

        // when
        val exists = saveFanNoteRepository.existsByMemberAndFanNote(member, fanNote)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("회원의 저장된 덕질노트를 페이지네이션으로 조회한다")
    fun `회원의 저장된 덕질노트를 페이지네이션으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        for (i in 1..5) {
            val fanNote = fanNoteRepository.save(
                FanNote(
                    title = "덕질노트$i",
                    subtitle = null,
                    content = "내용$i",
                    productionDate = LocalDate.now().plusDays(i.toLong()),
                    coverImageUrl = null
                )
            )
            saveFanNoteRepository.save(SaveFanNote(member = member, fanNote = fanNote))
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = saveFanNoteRepository.findByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(5)
        assertThat(page.totalElements).isEqualTo(5)
    }

    @Test
    @DisplayName("회원과 덕질노트로 저장을 삭제한다")
    fun `회원과 덕질노트로 저장을 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val fanNote = fanNoteRepository.save(
            FanNote(
                title = "삭제할 덕질노트",
                subtitle = null,
                content = null,
                productionDate = LocalDate.now(),
                coverImageUrl = null
            )
        )

        saveFanNoteRepository.save(SaveFanNote(member = member, fanNote = fanNote))
        testEntityManager.flush()

        // when
        saveFanNoteRepository.deleteByMemberAndFanNote(member, fanNote)
        testEntityManager.flush()

        // then
        val exists = saveFanNoteRepository.existsByMemberAndFanNote(member, fanNote)
        assertThat(exists).isFalse()
    }
}
