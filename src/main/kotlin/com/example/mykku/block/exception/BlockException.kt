package com.example.mykku.block.exception

import com.example.mykku.common.exception.BaseDomainException

class BlockException(
    errorCode: BlockErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {

        fun memberBlockNotFound(): BlockException =
            BlockException(BlockErrorCode.MEMBER_BLOCK_NOT_FOUND)

        fun memberAlreadyBlocked(): BlockException =
            BlockException(BlockErrorCode.MEMBER_ALREADY_BLOCKED)

        fun cannotBlockSelf(): BlockException =
            BlockException(BlockErrorCode.CANNOT_BLOCK_SELF)

        fun memberToBlockNotFound(): BlockException =
            BlockException(BlockErrorCode.MEMBER_TO_BLOCK_NOT_FOUND)

        fun keywordBlockNotFound(): BlockException =
            BlockException(BlockErrorCode.KEYWORD_BLOCK_NOT_FOUND)

        fun keywordAlreadyBlocked(): BlockException =
            BlockException(BlockErrorCode.KEYWORD_ALREADY_BLOCKED)

        fun keywordTooLong(): BlockException =
            BlockException(BlockErrorCode.KEYWORD_TOO_LONG)

        fun keywordEmpty(): BlockException =
            BlockException(BlockErrorCode.KEYWORD_EMPTY)

        fun keywordLimitExceeded(): BlockException =
            BlockException(BlockErrorCode.KEYWORD_LIMIT_EXCEEDED)
    }
}
