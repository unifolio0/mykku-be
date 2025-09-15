package com.example.mykku.fannote.repository

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

@DataJpaTest
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
    fun `덕질노트와 페이지를 함께 저장하고 조회한다`() {
        // given
        val fanNote = FanNote(
            title = "웹툰 스타일 덕질노트",
            subtitle = "옆으로 넘기는 만화",
            content = "네이버 웹툰처럼 볼 수 있는 콘텐츠",
            productionDate = LocalDate.of(2024, 1, 10),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover.jpg"
        )

        val page1 = FanNotePage(pageNumber = 1, imageUrl = "https://s3.amazonaws.com/mykku/page1.jpg")
        val page2 = FanNotePage(pageNumber = 2, imageUrl = "https://s3.amazonaws.com/mykku/page2.jpg")
        val page3 = FanNotePage(pageNumber = 3, imageUrl = "https://s3.amazonaws.com/mykku/page3.jpg")

        fanNote.addPage(page1)
        fanNote.addPage(page2)
        fanNote.addPage(page3)

        // when
        val savedFanNote = fanNoteRepository.save(fanNote)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundFanNote = fanNoteRepository.findByIdWithPages(savedFanNote.id)

        // then
        assertThat(foundFanNote).isNotNull
        assertThat(foundFanNote!!.pages).hasSize(3)
        assertThat(foundFanNote.pages[0].pageNumber).isEqualTo(1)
        assertThat(foundFanNote.pages[1].pageNumber).isEqualTo(2)
        assertThat(foundFanNote.pages[2].pageNumber).isEqualTo(3)
        assertThat(foundFanNote.pages[0].imageUrl).isEqualTo("https://s3.amazonaws.com/mykku/page1.jpg")
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
        val fanNotePage = fanNoteRepository.findAllOrderByProductionDateDesc(pageable)

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
    fun `존재하지 않는 ID로 덕질노트 조회 시 null을 반환한다`() {
        // given
        val nonExistentId = 999L

        // when
        val foundFanNote = fanNoteRepository.findByIdWithPages(nonExistentId)

        // then
        assertThat(foundFanNote).isNull()
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
        val result = fanNoteRepository.findAllOrderByProductionDateDesc(firstPage)

        // then
        assertThat(result.content).hasSize(10)
        assertThat(result.totalElements).isEqualTo(25)
        assertThat(result.totalPages).isEqualTo(3)
        assertThat(result.isFirst).isTrue()
        assertThat(result.isLast).isFalse()
        assertThat(result.content[0].title).isEqualTo("덕질노트 25")
    }
}
