package com.example.mykku.scrap.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.scrap.adapter.input.web.dto.CreateFolderWebRequest
import com.example.mykku.scrap.adapter.input.web.dto.FolderWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.FoldersWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.UpdateFolderWebRequest
import com.example.mykku.scrap.application.dto.CreateFolderCommand
import com.example.mykku.scrap.application.dto.DeleteFolderCommand
import com.example.mykku.scrap.application.dto.UpdateFolderCommand
import com.example.mykku.scrap.application.port.input.FolderUseCase
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
class FolderWebController(
    private val folderUseCase: FolderUseCase
) {

    @PostMapping
    fun createFolder(
        @RequestBody @Valid request: CreateFolderWebRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FolderWebResponse>> {
        val command = CreateFolderCommand(
            memberId = member.id.value,
            name = request.name,
            description = request.description
        )
        val result = folderUseCase.createFolder(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 생성되었습니다.",
                data = FolderWebResponse.from(result)
            )
        )
    }

    @GetMapping
    fun getFolders(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FoldersWebResponse>> {
        val result = folderUseCase.getFolders(member.id.value)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더 목록을 성공적으로 조회했습니다.",
                data = FoldersWebResponse.from(result)
            )
        )
    }

    @PutMapping("/{folderId}")
    fun updateFolder(
        @PathVariable folderId: Long,
        @RequestBody @Valid request: UpdateFolderWebRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<FolderWebResponse>> {
        val command = UpdateFolderCommand(
            memberId = member.id.value,
            folderId = folderId,
            name = request.name,
            description = request.description
        )
        val result = folderUseCase.updateFolder(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 수정되었습니다.",
                data = FolderWebResponse.from(result)
            )
        )
    }

    @DeleteMapping("/{folderId}")
    fun deleteFolder(
        @PathVariable folderId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = DeleteFolderCommand(
            memberId = member.id.value,
            folderId = folderId
        )
        folderUseCase.deleteFolder(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "폴더가 성공적으로 삭제되었습니다.",
                data = Unit
            )
        )
    }
}
