package com.example.mykku.scrap

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.FolderResponse
import com.example.mykku.scrap.dto.FoldersResponse
import com.example.mykku.scrap.dto.UpdateFolderRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/folders")
class FolderController(
    private val folderService: FolderService
) {

    @PostMapping
    fun createFolder(
        @RequestBody @Valid request: CreateFolderRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FolderResponse>> {
        val response = folderService.createFolder(request, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 생성되었습니다.",
                data = response
            )
        )
    }

    @GetMapping
    fun getFolders(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FoldersResponse>> {
        val response = folderService.getFolders(member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @PutMapping("/{folderId}")
    fun updateFolder(
        @PathVariable folderId: Long,
        @RequestBody @Valid request: UpdateFolderRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FolderResponse>> {
        val response = folderService.updateFolder(folderId, request, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 수정되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/{folderId}")
    fun deleteFolder(
        @PathVariable folderId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        folderService.deleteFolder(folderId, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 삭제되었습니다.",
                data = Unit
            )
        )
    }
}
