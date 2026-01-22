package com.example.mykku.block.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.block.application.dto.BlockKeywordCommand
import com.example.mykku.block.application.dto.BlockMemberCommand
import com.example.mykku.block.application.dto.GetKeywordBlocksQuery
import com.example.mykku.block.application.dto.GetMemberBlocksQuery
import com.example.mykku.block.application.dto.UnblockKeywordCommand
import com.example.mykku.block.application.dto.UnblockMemberCommand
import com.example.mykku.block.application.port.input.BlockKeywordUseCase
import com.example.mykku.block.application.port.input.BlockMemberUseCase
import com.example.mykku.block.application.port.input.GetKeywordBlocksUseCase
import com.example.mykku.block.application.port.input.GetMemberBlocksUseCase
import com.example.mykku.block.application.port.input.UnblockKeywordUseCase
import com.example.mykku.block.application.port.input.UnblockMemberUseCase
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
    private val blockMemberUseCase: BlockMemberUseCase,
    private val unblockMemberUseCase: UnblockMemberUseCase,
    private val getMemberBlocksUseCase: GetMemberBlocksUseCase,
    private val blockKeywordUseCase: BlockKeywordUseCase,
    private val unblockKeywordUseCase: UnblockKeywordUseCase,
    private val getKeywordBlocksUseCase: GetKeywordBlocksUseCase
) {

    @PostMapping("/members")
    fun blockMember(
        @CurrentMember member: Member,
        @RequestBody request: BlockMemberRequest
    ): ResponseEntity<ApiResponse<MemberBlockResponse>> {
        val command = BlockMemberCommand(
            blockerId = member.id,
            blockedMemberId = request.memberId
        )
        val result = blockMemberUseCase.blockMember(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(message = "사용자를 차단했습니다.", data = MemberBlockResponse.from(result)))
    }

    @DeleteMapping("/members/{memberId}")
    fun unblockMember(
        @CurrentMember member: Member,
        @PathVariable memberId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnblockMemberCommand(
            blockerId = member.id,
            blockedMemberId = memberId
        )
        unblockMemberUseCase.unblockMember(command)

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
        val query = GetMemberBlocksQuery(member.id, pageable)
        val result = getMemberBlocksUseCase.getMemberBlocks(query)

        return ResponseEntity.ok(ApiResponse(message = "차단한 사용자 목록을 조회했습니다.", data = MemberBlockListResponse.from(result)))
    }

    @PostMapping("/keywords")
    fun blockKeyword(
        @CurrentMember member: Member,
        @RequestBody request: BlockKeywordRequest
    ): ResponseEntity<ApiResponse<KeywordBlockResponse>> {
        val command = BlockKeywordCommand(
            memberId = member.id,
            keyword = request.keyword
        )
        val result = blockKeywordUseCase.blockKeyword(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(message = "키워드를 차단했습니다.", data = KeywordBlockResponse.from(result)))
    }

    @DeleteMapping("/keywords/{keyword}")
    fun unblockKeyword(
        @CurrentMember member: Member,
        @PathVariable keyword: String
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnblockKeywordCommand(
            memberId = member.id,
            keyword = keyword
        )
        unblockKeywordUseCase.unblockKeyword(command)

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
        val query = GetKeywordBlocksQuery(member.id, pageable)
        val result = getKeywordBlocksUseCase.getKeywordBlocks(query)

        return ResponseEntity.ok(ApiResponse(message = "차단한 키워드 목록을 조회했습니다.", data = KeywordBlockListResponse.from(result)))
    }
}
