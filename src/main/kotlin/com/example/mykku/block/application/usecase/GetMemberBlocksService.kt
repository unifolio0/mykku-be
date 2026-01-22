package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.GetMemberBlocksQuery
import com.example.mykku.block.application.dto.MemberBlockListResult
import com.example.mykku.block.application.dto.MemberBlockResult
import com.example.mykku.block.application.port.input.GetMemberBlocksUseCase
import com.example.mykku.block.application.port.output.MemberBlockRepository
import com.example.mykku.member.application.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMemberBlocksService(
    private val memberBlockRepository: MemberBlockRepository,
    private val memberRepository: MemberRepository
) : GetMemberBlocksUseCase {

    override fun getMemberBlocks(query: GetMemberBlocksQuery): MemberBlockListResult {
        val page = memberBlockRepository.findAllByBlockerId(query.memberId, query.pageable)

        val blocks = page.content.map { memberBlock ->
            val blockedMember = memberRepository.findByIdString(memberBlock.blockedId)
            MemberBlockResult.from(
                memberBlock,
                blockedMember?.nickname ?: "",
                blockedMember?.profileImage ?: ""
            )
        }

        return MemberBlockListResult(
            blocks = blocks,
            totalCount = page.totalElements,
            hasNext = page.hasNext()
        )
    }
}
