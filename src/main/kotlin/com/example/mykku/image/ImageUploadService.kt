package com.example.mykku.image

import com.example.mykku.config.S3Properties
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.image.dto.ImageUploadResult
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

@Service
@ConditionalOnProperty(name = ["aws.s3.enabled"], havingValue = "true", matchIfMissing = true)
class ImageUploadService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) {
    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp")
        private const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
        private val FILENAME_TS_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    }

    fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult> {
        return images.map { uploadImage(it) }
    }

    fun uploadImage(image: MultipartFile): ImageUploadResult {
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

        val url = s3Client.utilities()
            .getUrl { it.bucket(s3Properties.bucketName).key(key) }
            .toString()
        return ImageUploadResult(
            url = url,
            width = dimensions.first,
            height = dimensions.second
        )
    }

    private fun validateImage(image: MultipartFile) {
        if (image.isEmpty) {
            throw MykkuException(ErrorCode.IMAGE_FILE_EMPTY)
        }

        if (image.size > MAX_FILE_SIZE) {
            throw MykkuException(ErrorCode.IMAGE_FILE_TOO_LARGE)
        }

        val extension = getFileExtension(image.originalFilename)
        if (extension !in ALLOWED_EXTENSIONS) {
            throw MykkuException(ErrorCode.IMAGE_INVALID_FORMAT)
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

    private fun extractImageDimensions(imageBytes: ByteArray): Pair<Int, Int> {
        return try {
            val bufferedImage: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
                ?: throw MykkuException(ErrorCode.IMAGE_UNREADABLE)

            Pair(bufferedImage.width, bufferedImage.height)
        } catch (e: MykkuException) {
            // 도메인 예외는 그대로 전달
            throw e
        } catch (e: Exception) {
            throw MykkuException(ErrorCode.IMAGE_SIZE_EXTRACTION_FAILED)
        }
    }
}
