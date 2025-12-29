package com.example.mykku.scrap.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.FolderRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FolderWriterTest : BaseToolTest() {

    @Mock
    private lateinit var folderRepository: FolderRepository

    @Mock
    private lateinit var folderReader: FolderReader

    @InjectMocks
    private lateinit var folderWriter: FolderWriter

    @Test
    fun `createFolder는 폴더를 생성하고 저장한다`() {
        // given
        val member = createMockMember()
        val name = "새 폴더"
        val description = "폴더 설명"
        val savedFolder = Folder(
            id = 1L,
            member = member,
            name = name,
            description = description
        )

        whenever(folderRepository.save(any<Folder>()))
            .thenReturn(savedFolder)

        // when
        val result = folderWriter.createFolder(member, name, description)

        // then
        assertSame(savedFolder, result)
        verify(folderRepository).save(any<Folder>())
    }

    @Test
    fun `updateFolder는 폴더 정보를 수정한다`() {
        // given
        val member = createMockMember()
        val folderId = 1L
        val folder = Folder(
            id = folderId,
            member = member,
            name = "원래 이름",
            description = "원래 설명"
        )
        val newName = "새 이름"
        val newDescription = "새 설명"

        whenever(folderReader.getFolderById(folderId, member))
            .thenReturn(folder)
        whenever(folderRepository.save(folder))
            .thenReturn(folder)

        // when
        folderWriter.updateFolder(folderId, member, newName, newDescription)

        // then
        assertEquals(newName, folder.name)
        assertEquals(newDescription, folder.description)
        verify(folderRepository).save(folder)
    }

    @Test
    fun `deleteFolder는 폴더를 삭제한다`() {
        // given
        val member = createMockMember()
        val folderId = 1L
        val folder = Folder(
            id = folderId,
            member = member,
            name = "삭제할 폴더",
            description = null
        )

        whenever(folderReader.getFolderById(folderId, member))
            .thenReturn(folder)

        // when
        folderWriter.deleteFolder(folderId, member)

        // then
        verify(folderRepository).delete(folder)
    }

    @Test
    fun `updateFolder는 존재하지 않는 폴더면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()
        val folderId = 999L

        whenever(folderReader.getFolderById(folderId, member))
            .thenThrow(ScrapException.folderNotFound())

        // when & then
        assertThrows<ScrapException> {
            folderWriter.updateFolder(folderId, member, "새 이름", null)
        }
    }
}
