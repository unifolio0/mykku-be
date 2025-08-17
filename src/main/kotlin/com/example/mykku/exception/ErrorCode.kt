package com.example.mykku.exception

import com.example.mykku.board.domain.Board
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.Tag
import com.example.mykku.member.domain.Member
import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {
    // Daily Message
    DAILY_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "하루 덕담을 찾을 수 없습니다"),
    DAILY_MESSAGE_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "하루 덕담은 ${DailyMessage.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    DAILY_MESSAGE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    COMMENT_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다"),

    // Feed
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다"),
    FEED_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 내용은 ${Feed.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    FEED_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 이미지는 ${Feed.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    FEED_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 태그는 ${Feed.TAG_MAX_COUNT}개 이하여야 합니다"),
    LIKE_FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요한 피드를 찾을 수 없습니다"),
    LIKE_FEED_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 피드입니다"),

    // Tag
    TAG_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "태그는 ${Tag.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    TAG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "태그는 한글, 영문, 숫자만 사용할 수 있습니다"),

    // Board
    BOARD_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "게시판 제목은 ${Board.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    BOARD_DUPLICATE_TITLE(HttpStatus.BAD_REQUEST, "이미 존재하는 게시판 제목입니다"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다"),
    LIKE_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기한 게시판을 찾을 수 없습니다"),
    LIKE_BOARD_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 즐겨찾기한 게시판입니다"),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    MEMBER_NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "닉네임은 ${Member.NICKNAME_MAX_LENGTH}자 이하여야 합니다"),
    MEMBER_NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다"),

    // Comment
    FEED_COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 댓글은 ${FeedComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG(
        HttpStatus.BAD_REQUEST,
        "하루 덕담 댓글은 ${DailyMessageComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"
    ),
    LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED(
        HttpStatus.BAD_REQUEST,
        "이미 좋아요한 하루 덕담 댓글입니다"
    ),
    LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "좋아요한 하루 덕담 댓글을 찾을 수 없습니다"
    ),
    LIKE_FEED_COMMENT_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 피드 댓글입니다"),
    FEED_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "피드 댓글을 찾을 수 없습니다"),
    LIKE_FEED_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요한 피드 댓글을 찾을 수 없습니다"),

    // OAuth
    OAUTH_TOKEN_EXCHANGE_FAILED(HttpStatus.BAD_REQUEST, "OAuth 토큰 교환에 실패했습니다"),
    OAUTH_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "사용자 정보를 가져오는데 실패했습니다"),
    OAUTH_INVALID_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다"),
    OAUTH_INVALID_CLIENT(HttpStatus.UNAUTHORIZED, "유효하지 않은 클라이언트 정보입니다"),
    OAUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다"),
    OAUTH_EXTERNAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 오류가 발생했습니다"),

    // Image Upload
    IMAGE_FILE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다"),
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일 크기는 10MB 이하여야 합니다"),
    IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (지원 형식: jpeg, jpg, png, gif)"),
    IMAGE_UNREADABLE(HttpStatus.BAD_REQUEST, "이미지를 읽을 수 없습니다"),
    IMAGE_SIZE_EXTRACTION_FAILED(HttpStatus.BAD_REQUEST, "이미지 크기를 추출할 수 없습니다"),
    IMAGE_UPLOAD_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "ImageUploadService가 구성되지 않았습니다. 이미지 업로드가 비활성화된 환경에서는 images를 비워 주세요."),

    // Tag Creation
    TAG_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "태그 생성 중 오류가 발생했습니다."),

    // Apple OAuth
    APPLE_JWT_HEADER_MISSING_KID(HttpStatus.BAD_REQUEST, "JWT 헤더에 kid가 없습니다"),
    APPLE_JWT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰 형식입니다"),
    APPLE_PUBLIC_KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "공개 키를 찾을 수 없습니다"),

    // Validation
    INVALID_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "잘못된 정렬 방향입니다. 'asc' 또는 'desc'를 사용해주세요"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
}
