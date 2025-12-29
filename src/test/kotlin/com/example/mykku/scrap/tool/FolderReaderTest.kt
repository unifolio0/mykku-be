package com.example.mykku.scrap.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.FolderRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FolderReaderTest : BaseToolTest() {

    @Mock
    private lateinit var folderRepository: FolderRepository

    @InjectMocks
    private lateinit var folderReader: FolderReader

    @Test
    fun `getFolderById는 존재하는 폴더를 반환한다`() {
        // given
        val member = createMockMember()
        val folderId = 1L
        val mockFolder = Folder(
            id = folderId,
            member = member,
            name = "테스트 폴더",
            description = "설명"
        )

        whenever(folderRepository.findByMemberAndId(member, folderId))
            .thenReturn(mockFolder)

        // when
        val result = folderReader.getFolderById(folderId, member)

        // then
        assertSame(mockFolder, result)
    }

    @Test
    fun `getFolderById는 존재하지 않는 폴더면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()
        val folderId = 999L

        whenever(folderRepository.findByMemberAndId(member, folderId))
            .thenReturn(null)

        // when & then
        val exception = assertThrows<ScrapException> {
            folderReader.getFolderById(folderId, member)
        }

        assertEquals(ScrapErrorCode.FOLDER_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getFoldersByMember는 회원의 모든 폴더를 반환한다`() {
        // given
        val member = createMockMember()
        val folders = listOf(
            Folder(id = 1L, member = member, name = "폴더1", description = null),
            Folder(id = 2L, member = member, name = "폴더2", description = "설명2")
        )

        whenever(folderRepository.findByMember(member))
            .thenReturn(folders)

        // when
        val result = folderReader.getFoldersByMember(member)

        // then
        assertEquals(2, result.size)
        assertSame(folders, result)
    }

    @Test
    fun `existsByName은 중복된 이름이 존재하면 true를 반환한다`() {
        // given
        val member = createMockMember()
        val folderName = "중복된 폴더"

        whenever(folderRepository.existsByMemberAndName(member, folderName))
            .thenReturn(true)

        // when
        val result = folderReader.existsByName(member, folderName)

        // then
        assertEquals(true, result)
    }

    @Test
    fun `existsByName은 중복되지 않은 이름이면 false를 반환한다`() {
        // given
        val member = createMockMember()
        val folderName = "새로운 폴더"

        whenever(folderRepository.existsByMemberAndName(member, folderName))
            .thenReturn(false)

        // when
        val result = folderReader.existsByName(member, folderName)

        // then
        assertEquals(false, result)
    }
}
