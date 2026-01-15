package com.example.mykku.block

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.block.dto.BlockKeywordRequest
import com.example.mykku.block.dto.BlockMemberRequest
import com.example.mykku.block.dto.KeywordBlockListResponse
import com.example.mykku.block.dto.KeywordBlockResponse
import com.example.mykku.block.dto.MemberBlockListResponse
import com.example.mykku.block.dto.MemberBlockResponse
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/blocks")
class BlockController(
    private val blockService: BlockService
) {

    @PostMapping("/members")
    fun blockMember(
        @CurrentMember member: Member,
        @RequestBody request: BlockMemberRequest
    ): ResponseEntity<ApiResponse<MemberBlockResponse>> {
        val response = blockService.blockMember(member, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(message = "사용자를 차단했습니다.", data = response))
    }

    @DeleteMapping("/members/{memberId}")
    fun unblockMember(
        @CurrentMember member: Member,
        @PathVariable memberId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        blockService.unblockMember(member, memberId)
        return ResponseEntity.ok(ApiResponse(message = "사용자 차단을 해제했습니다.", data = Unit))
    }

    @GetMapping("/members")
    fun getMemberBlocks(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<MemberBlockListResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )
        val response = blockService.getMemberBlocks(member, pageable)
        return ResponseEntity.ok(ApiResponse(message = "차단한 사용자 목록을 조회했습니다.", data = response))
    }

    @PostMapping("/keywords")
    fun blockKeyword(
        @CurrentMember member: Member,
        @RequestBody request: BlockKeywordRequest
    ): ResponseEntity<ApiResponse<KeywordBlockResponse>> {
        val response = blockService.blockKeyword(member, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(message = "키워드를 차단했습니다.", data = response))
    }

    @DeleteMapping("/keywords/{keyword}")
    fun unblockKeyword(
        @CurrentMember member: Member,
        @PathVariable keyword: String
    ): ResponseEntity<ApiResponse<Unit>> {
        blockService.unblockKeyword(member, keyword)
        return ResponseEntity.ok(ApiResponse(message = "키워드 차단을 해제했습니다.", data = Unit))
    }

    @GetMapping("/keywords")
    fun getKeywordBlocks(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<KeywordBlockListResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )
        val response = blockService.getKeywordBlocks(member, pageable)
        return ResponseEntity.ok(ApiResponse(message = "차단한 키워드 목록을 조회했습니다.", data = response))
    }
}
