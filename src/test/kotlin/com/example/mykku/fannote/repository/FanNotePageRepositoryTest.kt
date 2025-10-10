package com.example.mykku.fannote.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate

class FanNotePageRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var fanNotePageRepository: FanNotePageRepository

    @Autowired
    private lateinit var fanNoteRepository: FanNoteRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `덕질노트 ID로 페이지를 조회하면 페이지 번호 순으로 정렬된다`() {
        // given
        val fanNote = FanNote(
            title = "테스트 덕질노트",
            subtitle = "서브타이틀",
            content = "내용",
            productionDate = LocalDate.of(2024, 1, 10),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover.jpg"
        )
        val savedFanNote = fanNoteRepository.save(fanNote)

        val page3 =
            FanNotePage(pageNumber = 3, imageUrl = "https://s3.amazonaws.com/mykku/page3.jpg", fanNote = savedFanNote)
        val page1 =
            FanNotePage(pageNumber = 1, imageUrl = "https://s3.amazonaws.com/mykku/page1.jpg", fanNote = savedFanNote)
        val page2 =
            FanNotePage(pageNumber = 2, imageUrl = "https://s3.amazonaws.com/mykku/page2.jpg", fanNote = savedFanNote)

        fanNotePageRepository.save(page3)
        fanNotePageRepository.save(page1)
        fanNotePageRepository.save(page2)

        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(savedFanNote.id)

        // then
        assertThat(pages).hasSize(3)
        assertThat(pages[0].pageNumber).isEqualTo(1)
        assertThat(pages[1].pageNumber).isEqualTo(2)
        assertThat(pages[2].pageNumber).isEqualTo(3)
        assertThat(pages[0].imageUrl).isEqualTo("https://s3.amazonaws.com/mykku/page1.jpg")
        assertThat(pages[1].imageUrl).isEqualTo("https://s3.amazonaws.com/mykku/page2.jpg")
        assertThat(pages[2].imageUrl).isEqualTo("https://s3.amazonaws.com/mykku/page3.jpg")
    }

    @Test
    fun `존재하지 않는 덕질노트 ID로 페이지 조회 시 빈 리스트를 반환한다`() {
        // given
        val nonExistentId = 999L

        // when
        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(nonExistentId)

        // then
        assertThat(pages).isEmpty()
    }
}
