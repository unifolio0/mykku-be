package com.example.mykku.scrap

import com.example.mykku.BaseServiceTest
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.UpdateFolderRequest
import com.example.mykku.scrap.tool.FolderReader
import com.example.mykku.scrap.tool.FolderWriter
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class FolderServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var folderReader: FolderReader

    @Mock
    private lateinit var folderWriter: FolderWriter

    @InjectMocks
    private lateinit var folderService: FolderService

    @Test
    fun `createFolder는 폴더를 생성하고 DTO로 반환한다`() {
        // given
        val member = createTestMember()
        val request = CreateFolderRequest(
            name = "새 폴더",
            description = "폴더 설명"
        )
        val createdFolder = Folder(
            id = 1L,
            member = member,
            name = request.name,
            description = request.description
        ).also { initializeBaseEntityFields(it) }

        whenever(folderWriter.createFolder(member, request.name, request.description))
            .thenReturn(createdFolder)

        // when
        val result = folderService.createFolder(request, member)

        // then
        assertEquals(1L, result.id)
        assertEquals("새 폴더", result.name)
        assertEquals("폴더 설명", result.description)
        verify(folderWriter).createFolder(member, request.name, request.description)
    }

    @Test
    fun `getFolders는 회원의 모든 폴더 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val folders = listOf(
            Folder(id = 1L, member = member, name = "폴더1", description = null).also { initializeBaseEntityFields(it) },
            Folder(id = 2L, member = member, name = "폴더2", description = "설명2").also { initializeBaseEntityFields(it) },
            Folder(id = 3L, member = member, name = "폴더3", description = null).also { initializeBaseEntityFields(it) }
        )

        whenever(folderReader.getFoldersByMember(member))
            .thenReturn(folders)

        // when
        val result = folderService.getFolders(member)

        // then
        assertEquals(3, result.folders.size)
        assertEquals("폴더1", result.folders[0].name)
        assertEquals("폴더2", result.folders[1].name)
        assertEquals("폴더3", result.folders[2].name)
        verify(folderReader).getFoldersByMember(member)
    }

    @Test
    fun `updateFolder는 폴더 정보를 수정하고 DTO로 반환한다`() {
        // given
        val member = createTestMember()
        val folderId = 1L
        val request = UpdateFolderRequest(
            name = "수정된 폴더",
            description = "수정된 설명"
        )
        val updatedFolder = Folder(
            id = folderId,
            member = member,
            name = request.name,
            description = request.description
        ).also { initializeBaseEntityFields(it) }

        whenever(folderWriter.updateFolder(folderId, member, request.name, request.description))
            .thenReturn(updatedFolder)

        // when
        val result = folderService.updateFolder(folderId, request, member)

        // then
        assertEquals(folderId, result.id)
        assertEquals("수정된 폴더", result.name)
        assertEquals("수정된 설명", result.description)
        verify(folderWriter).updateFolder(folderId, member, request.name, request.description)
    }

    @Test
    fun `deleteFolder는 폴더를 삭제한다`() {
        // given
        val member = createTestMember()
        val folderId = 1L

        // when
        folderService.deleteFolder(folderId, member)

        // then
        verify(folderWriter).deleteFolder(folderId, member)
    }
}
