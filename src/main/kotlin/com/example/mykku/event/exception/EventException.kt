package com.example.mykku.event.exception

import com.example.mykku.common.exception.BaseDomainException

class EventException(
    errorCode: EventErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        fun eventNotFound(): EventException = EventException(EventErrorCode.EVENT_NOT_FOUND)
        
        fun eventImageLimitExceeded(): EventException = EventException(EventErrorCode.EVENT_IMAGE_LIMIT_EXCEEDED)
        
        fun invalidEventStatus(): EventException = EventException(EventErrorCode.INVALID_EVENT_STATUS)
        
        fun imageInvalidDimensions(): EventException = EventException(EventErrorCode.IMAGE_INVALID_DIMENSIONS)
    }
}
