package com.example.mykku.image.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class ImageErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    
    IMAGE_FILE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다"),
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일 크기가 너무 큽니다"),
    IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다"),
    IMAGE_UNREADABLE(HttpStatus.BAD_REQUEST, "이미지 파일을 읽을 수 없습니다"),

    IMAGE_SIZE_EXTRACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 크기를 추출할 수 없습니다"),
    IMAGE_INVALID_DIMENSIONS(HttpStatus.BAD_REQUEST, "이미지 크기가 유효하지 않습니다"),

    IMAGE_UPLOAD_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "이미지 업로드 서비스를 사용할 수 없습니다")
}