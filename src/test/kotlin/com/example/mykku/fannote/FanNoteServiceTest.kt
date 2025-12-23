package com.example.mykku.fannote

import com.example.mykku.BaseServiceTest
import com.example.mykku.fannote.exception.FanNoteException
import com.example.mykku.fannote.exception.FanNoteErrorCode
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

class FanNoteServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var fanNoteQueryPort: FanNoteQueryPort

    @InjectMocks
    private lateinit var fanNoteService: FanNoteService

    @Test
    fun `덕질노트 목록을 페이지네이션하여 조회한다`() {
        // given
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "productionDate"))
        val fanNote1 = createFanNote(1L, "덕질노트1", LocalDate.of(2024, 1, 1))
        val fanNote2 = createFanNote(2L, "덕질노트2", LocalDate.of(2024, 1, 2))
        val fanNotePage = PageImpl(listOf(fanNote1, fanNote2), pageable, 2)

        given(fanNoteQueryPort.findAllWithPagination(pageable)).willReturn(fanNotePage)

        // when
        val result = fanNoteService.getFanNoteList(pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].id).isEqualTo(1L)
        assertThat(result.content[0].title).isEqualTo("덕질노트1")
        assertThat(result.content[1].id).isEqualTo(2L)
        assertThat(result.content[1].title).isEqualTo("덕질노트2")

        verify(fanNoteQueryPort).findAllWithPagination(pageable)
    }

    @Test
    fun `덕질노트 상세 정보를 조회한다`() {
        // given
        val fanNoteId = 1L
        val fanNote = createFanNote(fanNoteId, "덕질노트", LocalDate.of(2024, 1, 1))
        val page1 = FanNotePage(1L, 1, "https://s3.amazonaws.com/mykku/page1.jpg", fanNote)
        val page2 = FanNotePage(2L, 2, "https://s3.amazonaws.com/mykku/page2.jpg", fanNote)

        val pages = listOf(page1, page2)

        given(fanNoteQueryPort.findById(fanNoteId)).willReturn(fanNote)
        given(fanNoteQueryPort.findPagesByFanNoteId(fanNoteId)).willReturn(pages)

        // when
        val result = fanNoteService.getFanNoteDetail(fanNoteId)

        // then
        assertThat(result.id).isEqualTo(fanNoteId)
        assertThat(result.title).isEqualTo("덕질노트")
        assertThat(result.pages).hasSize(2)
        assertThat(result.pages[0].pageNumber).isEqualTo(1)
        assertThat(result.pages[0].imageUrl).isEqualTo("https://s3.amazonaws.com/mykku/page1.jpg")
        assertThat(result.pages[1].pageNumber).isEqualTo(2)

        verify(fanNoteQueryPort).findById(fanNoteId)
        verify(fanNoteQueryPort).findPagesByFanNoteId(fanNoteId)
    }

    @Test
    fun `존재하지 않는 덕질노트 조회 시 예외가 발생한다`() {
        // given
        val fanNoteId = 999L
        given(fanNoteQueryPort.findById(fanNoteId)).willThrow(FanNoteException(FanNoteErrorCode.FAN_NOTE_NOT_FOUND))

        // when & then
        assertThatThrownBy { fanNoteService.getFanNoteDetail(fanNoteId) }
            .isInstanceOf(FanNoteException::class.java)
            .hasMessageContaining("덕질노트를 찾을 수 없습니다")
    }

    @Test
    fun `빈 덕질노트 목록을 조회한다`() {
        // given
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "productionDate"))
        val emptyPage = PageImpl<FanNote>(emptyList(), pageable, 0)

        given(fanNoteQueryPort.findAllWithPagination(pageable)).willReturn(emptyPage)

        // when
        val result = fanNoteService.getFanNoteList(pageable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
        assertThat(result.totalPages).isEqualTo(0)

        verify(fanNoteQueryPort).findAllWithPagination(pageable)
    }

    @Test
    fun `페이지가 없는 덕질노트 상세 정보를 조회한다`() {
        // given
        val fanNoteId = 1L
        val fanNote = createFanNote(fanNoteId, "페이지 없는 덕질노트", LocalDate.of(2024, 1, 1))

        given(fanNoteQueryPort.findById(fanNoteId)).willReturn(fanNote)
        given(fanNoteQueryPort.findPagesByFanNoteId(fanNoteId)).willReturn(emptyList())

        // when
        val result = fanNoteService.getFanNoteDetail(fanNoteId)

        // then
        assertThat(result.id).isEqualTo(fanNoteId)
        assertThat(result.title).isEqualTo("페이지 없는 덕질노트")
        assertThat(result.pages).isEmpty()

        verify(fanNoteQueryPort).findById(fanNoteId)
        verify(fanNoteQueryPort).findPagesByFanNoteId(fanNoteId)
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
