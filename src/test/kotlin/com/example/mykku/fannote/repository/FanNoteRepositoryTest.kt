package com.example.mykku.fannote.repository

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.util.DatabaseCleaner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

@DataJpaTest
@ExtendWith(DatabaseCleaner::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FanNoteRepositoryTest {

    @Autowired
    private lateinit var fanNoteRepository: FanNoteRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `덕질노트를 저장하고 조회한다`() {
        // given
        val fanNote = FanNote(
            title = "테스트 덕질노트",
            subtitle = "서브타이틀",
            content = "덕질노트 내용",
            productionDate = LocalDate.of(2024, 1, 15),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover.jpg"
        )

        // when
        val savedFanNote = fanNoteRepository.save(fanNote)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundFanNote = fanNoteRepository.findById(savedFanNote.id).orElse(null)

        // then
        assertThat(foundFanNote).isNotNull
        assertThat(foundFanNote.title).isEqualTo("테스트 덕질노트")
        assertThat(foundFanNote.subtitle).isEqualTo("서브타이틀")
        assertThat(foundFanNote.content).isEqualTo("덕질노트 내용")
        assertThat(foundFanNote.productionDate).isEqualTo(LocalDate.of(2024, 1, 15))
        assertThat(foundFanNote.coverImageUrl).isEqualTo("https://s3.amazonaws.com/mykku/cover.jpg")
    }

    @Test
    fun `덕질노트를 저장하고 ID로 조회한다`() {
        // given
        val fanNote = FanNote(
            title = "웹툰 스타일 덕질노트",
            subtitle = "옆으로 넘기는 만화",
            content = "네이버 웹툰처럼 볼 수 있는 콘텐츠",
            productionDate = LocalDate.of(2024, 1, 10),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover.jpg"
        )

        // when
        val savedFanNote = fanNoteRepository.save(fanNote)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundFanNote = fanNoteRepository.findById(savedFanNote.id).orElse(null)

        // then
        assertThat(foundFanNote).isNotNull
        assertThat(foundFanNote.title).isEqualTo("웹툰 스타일 덕질노트")
        assertThat(foundFanNote.subtitle).isEqualTo("옆으로 넘기는 만화")
        assertThat(foundFanNote.content).isEqualTo("네이버 웹툰처럼 볼 수 있는 콘텐츠")
    }

    @Test
    fun `제작일 기준 내림차순으로 덕질노트 목록을 조회한다`() {
        // given
        val fanNote1 = FanNote(
            title = "첫 번째 덕질노트",
            subtitle = null,
            content = "내용1",
            productionDate = LocalDate.of(2024, 1, 10),
            coverImageUrl = null
        )

        val fanNote2 = FanNote(
            title = "두 번째 덕질노트",
            subtitle = null,
            content = "내용2",
            productionDate = LocalDate.of(2024, 1, 15),
            coverImageUrl = null
        )

        val fanNote3 = FanNote(
            title = "세 번째 덕질노트",
            subtitle = null,
            content = "내용3",
            productionDate = LocalDate.of(2024, 1, 5),
            coverImageUrl = null
        )

        fanNoteRepository.save(fanNote1)
        fanNoteRepository.save(fanNote2)
        fanNoteRepository.save(fanNote3)
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "productionDate"))
        val fanNotePage = fanNoteRepository.findAll(pageable)

        // then
        assertThat(fanNotePage.content).hasSize(3)
        assertThat(fanNotePage.content[0].title).isEqualTo("두 번째 덕질노트")
        assertThat(fanNotePage.content[0].productionDate).isEqualTo(LocalDate.of(2024, 1, 15))
        assertThat(fanNotePage.content[1].title).isEqualTo("첫 번째 덕질노트")
        assertThat(fanNotePage.content[1].productionDate).isEqualTo(LocalDate.of(2024, 1, 10))
        assertThat(fanNotePage.content[2].title).isEqualTo("세 번째 덕질노트")
        assertThat(fanNotePage.content[2].productionDate).isEqualTo(LocalDate.of(2024, 1, 5))
    }

    @Test
    fun `존재하지 않는 ID로 덕질노트 조회 시 빈 Optional을 반환한다`() {
        // given
        val nonExistentId = 999L

        // when
        val foundFanNote = fanNoteRepository.findById(nonExistentId)

        // then
        assertThat(foundFanNote).isEmpty
    }

    @Test
    fun `덕질노트 존재 여부를 확인한다`() {
        // given
        val fanNote = FanNote(
            title = "존재 확인용 덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.of(2024, 1, 1),
            coverImageUrl = null
        )
        val savedFanNote = fanNoteRepository.save(fanNote)
        testEntityManager.flush()

        // when
        val exists = fanNoteRepository.existsById(savedFanNote.id)
        val notExists = fanNoteRepository.existsById(999L)

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `페이지네이션이 올바르게 동작한다`() {
        // given
        for (i in 1..25) {
            val fanNote = FanNote(
                title = "덕질노트 $i",
                subtitle = null,
                content = "내용 $i",
                productionDate = LocalDate.of(2024, 1, i),
                coverImageUrl = null
            )
            fanNoteRepository.save(fanNote)
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val firstPage = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "productionDate"))
        val result = fanNoteRepository.findAll(firstPage)

        // then
        assertThat(result.content).hasSize(10)
        assertThat(result.totalElements).isEqualTo(25)
        assertThat(result.totalPages).isEqualTo(3)
        assertThat(result.isFirst).isTrue()
        assertThat(result.isLast).isFalse()
        assertThat(result.content[0].title).isEqualTo("덕질노트 25")
    }
}
