package com.example.mykku.image.exception

import com.example.mykku.common.exception.BaseDomainException

/**
 * Image 도메인 예외 클래스
 */
class ImageException(
    errorCode: ImageErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun imageFileEmpty(): ImageException =
            ImageException(ImageErrorCode.IMAGE_FILE_EMPTY)

        fun imageFileTooLarge(): ImageException =
            ImageException(ImageErrorCode.IMAGE_FILE_TOO_LARGE)

        fun imageInvalidFormat(): ImageException =
            ImageException(ImageErrorCode.IMAGE_INVALID_FORMAT)

        fun imageUnreadable(): ImageException =
            ImageException(ImageErrorCode.IMAGE_UNREADABLE)

        fun imageSizeExtractionFailed(): ImageException =
            ImageException(ImageErrorCode.IMAGE_SIZE_EXTRACTION_FAILED)

        fun imageInvalidDimensions(): ImageException =
            ImageException(ImageErrorCode.IMAGE_INVALID_DIMENSIONS)

        fun imageUploadServiceUnavailable(): ImageException =
            ImageException(ImageErrorCode.IMAGE_UPLOAD_SERVICE_UNAVAILABLE)
    }
}