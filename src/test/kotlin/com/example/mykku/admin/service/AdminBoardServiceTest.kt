package com.example.mykku.admin.service

import com.example.mykku.admin.dto.board.BoardCreateRequest
import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.dto.CreateBoardCommand
import com.example.mykku.board.application.port.input.CreateBoardUseCase
import com.example.mykku.board.application.port.input.ListBoardsUseCase
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile

@DisplayName("AdminBoardService 테스트")
class AdminBoardServiceTest {

    private val createBoardUseCase: CreateBoardUseCase = mock()
    private val listBoardsUseCase: ListBoardsUseCase = mock()
    private val imageUploadService: ImageUploadService = mock()
    private val adminBoardService = AdminBoardService(createBoardUseCase, listBoardsUseCase, imageUploadService)

    @Test
    @DisplayName("게시판 생성 시 로고를 board-images 경로로 업로드한다")
    fun `게시판 생성 - 로고 업로드 경로`() {
        val logoUrl = "https://cdn.test.com/board-images/20260725123000_abc.png"
        val request = BoardCreateRequest(title = "테스트 게시판", logo = logoFile())
        whenever(imageUploadService.uploadImage(any(), any()))
            .thenReturn(ImageUploadResult(url = logoUrl, width = 100, height = 100))
        whenever(createBoardUseCase.create(any()))
            .thenReturn(BoardResult(id = 1L, title = request.title, logo = logoUrl))

        adminBoardService.create(request)

        verify(imageUploadService).uploadImage(request.logo, "board-images")
    }

    @Test
    @DisplayName("업로드된 로고 URL이 게시판 생성 커맨드에 전달된다")
    fun `게시판 생성 - 커맨드 로고 URL`() {
        val logoUrl = "https://cdn.test.com/board-images/20260725123000_abc.png"
        val request = BoardCreateRequest(title = "테스트 게시판", logo = logoFile())
        whenever(imageUploadService.uploadImage(any(), any()))
            .thenReturn(ImageUploadResult(url = logoUrl, width = 100, height = 100))
        whenever(createBoardUseCase.create(any()))
            .thenReturn(BoardResult(id = 1L, title = request.title, logo = logoUrl))

        adminBoardService.create(request)

        val captor = argumentCaptor<CreateBoardCommand>()
        verify(createBoardUseCase).create(captor.capture())
        assertThat(captor.firstValue.logo).isEqualTo(logoUrl)
        assertThat(captor.firstValue.title).isEqualTo("테스트 게시판")
    }

    @Test
    @DisplayName("게시판 생성이 실패하면 업로드된 로고를 삭제한다")
    fun `게시판 생성 실패 - 로고 삭제`() {
        val logoUrl = "https://cdn.test.com/board-images/20260725123000_abc.png"
        val request = BoardCreateRequest(title = "테스트 게시판", logo = logoFile())
        whenever(imageUploadService.uploadImage(any(), any()))
            .thenReturn(ImageUploadResult(url = logoUrl, width = 100, height = 100))
        whenever(createBoardUseCase.create(any())).thenThrow(IllegalStateException("저장 실패"))

        runCatching { adminBoardService.create(request) }

        verify(imageUploadService).delete(logoUrl)
    }

    private fun logoFile() = MockMultipartFile("logo", "logo.png", "image/png", byteArrayOf(1, 2, 3))
}
