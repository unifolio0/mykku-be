package com.example.mykku.image

import com.example.mykku.config.S3Properties
import com.example.mykku.image.dto.FanNoteImagesUploadResult
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.image.exception.ImageException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

@Service
@ConditionalOnProperty(name = ["aws.s3.enabled"], havingValue = "true")
class S3ImageUploadService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) : ImageUploadService {
    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp")
        private const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
        private val FILENAME_TS_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val FILENAME_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    override fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult> {
        return images.map { uploadImage(it) }
    }

    override fun uploadImage(image: MultipartFile): ImageUploadResult {
        validateImage(image)

        // 이미지 크기 추출
        val imageBytes = image.bytes
        val dimensions = extractImageDimensions(imageBytes)

        val fileName = generateFileName(image.originalFilename)
        val key = "feed-images/$fileName"

        val extensionForContentType = getFileExtension(image.originalFilename)
        val contentType = image.contentType ?: when (extensionForContentType) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucketName)
            .key(key)
            .contentType(contentType)
            .contentLength(imageBytes.size.toLong())
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromBytes(imageBytes)
        )

        val url = buildImageUrl(key)
        return ImageUploadResult(
            url = url,
            width = dimensions.first,
            height = dimensions.second
        )
    }

    private fun validateImage(image: MultipartFile) {
        if (image.isEmpty) {
            throw ImageException.imageFileEmpty()
        }

        if (image.size > MAX_FILE_SIZE) {
            throw ImageException.imageFileTooLarge()
        }

        val extension = getFileExtension(image.originalFilename)
        if (extension !in ALLOWED_EXTENSIONS) {
            throw ImageException.imageInvalidFormat()
        }
    }

    private fun generateFileName(originalFilename: String?): String {
        val extension = getFileExtension(originalFilename)
        val timestamp = LocalDateTime.now().format(FILENAME_TS_FORMATTER)
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "${timestamp}_${uuid}.$extension"
    }

    private fun getFileExtension(filename: String?): String {
        return filename?.substringAfterLast(".", "")?.lowercase() ?: ""
    }

    /**
     * 이미지 URL 생성
     * CloudFront 도메인이 설정되어 있으면 CloudFront URL, 없으면 S3 직접 URL 반환
     */
    private fun buildImageUrl(key: String): String {
        return if (!s3Properties.cloudfrontDomain.isNullOrBlank()) {
            "https://${s3Properties.cloudfrontDomain}/$key"
        } else {
            s3Client.utilities()
                .getUrl { it.bucket(s3Properties.bucketName).key(key) }
                .toString()
        }
    }

    private fun extractImageDimensions(imageBytes: ByteArray): Pair<Int, Int> {
        return try {
            val bufferedImage: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
                ?: throw ImageException.imageUnreadable()

            Pair(bufferedImage.width, bufferedImage.height)
        } catch (e: ImageException) {
            // 도메인 예외는 그대로 전달
            throw e
        } catch (e: Exception) {
            throw ImageException.imageSizeExtractionFailed()
        }
    }

    /**
     * 팬노트 이미지 업로드 (커버 + 페이지)
     * @param coverImage 커버 이미지 (optional)
     * @param pageImages 페이지 이미지 리스트 (optional)
     * @return 업로드된 이미지 URL 정보
     */
    override fun uploadFanNoteImages(
        coverImage: MultipartFile?,
        pageImages: List<MultipartFile>?
    ): FanNoteImagesUploadResult {
        val folderName = createFanNoteFolder()

        val coverUrl = coverImage?.takeIf { !it.isEmpty }?.let {
            uploadFanNoteImageToS3(it, folderName, 0)
        }

        val pageUrls = pageImages?.filterNot { it.isEmpty }?.mapIndexed { index, file ->
            uploadFanNoteImageToS3(file, folderName, index + 1)
        } ?: emptyList()

        return FanNoteImagesUploadResult(coverUrl, pageUrls)
    }

    /**
     * 팬노트별 고유 폴더명 생성
     * 형식: yyyy-MM-dd-{UUID 앞 8자리}
     */
    private fun createFanNoteFolder(): String {
        val date = LocalDate.now().format(DATE_FORMATTER)
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        return "$date-$uuid"
    }

    /**
     * 팬노트 이미지를 S3에 업로드
     * @param image 업로드할 이미지
     * @param folderName 팬노트 폴더명
     * @param pageNumber 페이지 번호 (0=커버, 1~=페이지)
     * @return 업로드된 이미지 URL
     */
    private fun uploadFanNoteImageToS3(
        image: MultipartFile,
        folderName: String,
        pageNumber: Int
    ): String {
        validateImage(image)

        val imageBytes = image.bytes
        val fileName = generateFanNoteFileName(image.originalFilename, pageNumber)
        val key = "fan-note/$folderName/$fileName"

        val extensionForContentType = getFileExtension(image.originalFilename)
        val contentType = image.contentType ?: when (extensionForContentType) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucketName)
            .key(key)
            .contentType(contentType)
            .contentLength(imageBytes.size.toLong())
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromBytes(imageBytes)
        )

        return buildImageUrl(key)
    }

    /**
     * 팬노트 파일명 생성
     * 형식: yyyyMMdd-{pageNumber}.{extension}
     */
    private fun generateFanNoteFileName(originalFilename: String?, pageNumber: Int): String {
        val extension = getFileExtension(originalFilename)
        val date = LocalDate.now().format(FILENAME_DATE_FORMATTER)
        return "$date-$pageNumber.$extension"
    }
}
