package com.example.mykku.fannote.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.repository.FanNoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class FanNoteReaderTest {

    @Mock
    private lateinit var fanNoteRepository: FanNoteRepository

    @InjectMocks
    private lateinit var fanNoteReader: FanNoteReader

    @Test
    fun `페이지네이션으로 덕질노트 목록을 조회한다`() {
        // given
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "productionDate"))
        val fanNote1 = createFanNote(1L, "덕질노트1", LocalDate.of(2024, 1, 15))
        val fanNote2 = createFanNote(2L, "덕질노트2", LocalDate.of(2024, 1, 10))
        val fanNotePage = PageImpl(listOf(fanNote1, fanNote2), pageable, 2)

        given(fanNoteRepository.findAllOrderByProductionDateDesc(pageable)).willReturn(fanNotePage)

        // when
        val result = fanNoteReader.findAllWithPagination(pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].id).isEqualTo(1L)
        assertThat(result.content[0].title).isEqualTo("덕질노트1")
        assertThat(result.content[1].id).isEqualTo(2L)
        assertThat(result.content[1].title).isEqualTo("덕질노트2")
        assertThat(result.totalElements).isEqualTo(2)

        verify(fanNoteRepository).findAllOrderByProductionDateDesc(pageable)
    }

    @Test
    fun `ID로 덕질노트와 페이지를 함께 조회한다`() {
        // given
        val fanNoteId = 1L
        val fanNote = createFanNote(fanNoteId, "덕질노트", LocalDate.of(2024, 1, 15))
        val page1 = FanNotePage(1L, 1, "https://s3.amazonaws.com/mykku/page1.jpg")
        val page2 = FanNotePage(2L, 2, "https://s3.amazonaws.com/mykku/page2.jpg")
        fanNote.addPage(page1)
        fanNote.addPage(page2)

        given(fanNoteRepository.findByIdWithPages(fanNoteId)).willReturn(fanNote)

        // when
        val result = fanNoteReader.findByIdWithPages(fanNoteId)

        // then
        assertThat(result.id).isEqualTo(fanNoteId)
        assertThat(result.title).isEqualTo("덕질노트")
        assertThat(result.pages).hasSize(2)
        assertThat(result.pages[0].pageNumber).isEqualTo(1)
        assertThat(result.pages[1].pageNumber).isEqualTo(2)

        verify(fanNoteRepository).findByIdWithPages(fanNoteId)
    }

    @Test
    fun `존재하지 않는 덕질노트 조회 시 예외를 발생시킨다`() {
        // given
        val fanNoteId = 999L
        given(fanNoteRepository.findByIdWithPages(fanNoteId)).willReturn(null)

        // when & then
        assertThatThrownBy { fanNoteReader.findByIdWithPages(fanNoteId) }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAN_NOTE_NOT_FOUND)

        verify(fanNoteRepository).findByIdWithPages(fanNoteId)
    }

    @Test
    fun `덕질노트 존재 여부를 확인한다`() {
        // given
        val fanNoteId = 1L
        given(fanNoteRepository.existsById(fanNoteId)).willReturn(true)

        // when
        val result = fanNoteReader.existsById(fanNoteId)

        // then
        assertThat(result).isTrue()
        verify(fanNoteRepository).existsById(fanNoteId)
    }

    @Test
    fun `존재하지 않는 덕질노트의 존재 여부를 확인한다`() {
        // given
        val fanNoteId = 999L
        given(fanNoteRepository.existsById(fanNoteId)).willReturn(false)

        // when
        val result = fanNoteReader.existsById(fanNoteId)

        // then
        assertThat(result).isFalse()
        verify(fanNoteRepository).existsById(fanNoteId)
    }

    private fun createFanNote(id: Long, title: String, productionDate: LocalDate): FanNote {
        return FanNote(
            id = id,
            title = title,
            subtitle = "$title 서브타이틀",
            content = "$title 내용",
            productionDate = productionDate,
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover_$id.jpg"
        )
    }
}
