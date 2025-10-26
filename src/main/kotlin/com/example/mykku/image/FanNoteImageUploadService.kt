package com.example.mykku.image

import com.example.mykku.config.S3Properties
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.image.exception.ImageException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

@Service
class FanNoteImageUploadService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) {
    companion object {
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val FILENAME_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    /**
     * 팬노트별 고유 폴더명 생성
     * 형식: yyyy-MM-dd-{UUID 앞 8자리}
     */
    fun createFanNoteFolder(): String {
        val date = LocalDate.now().format(DATE_FORMATTER)
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        return "$date-$uuid"
    }

    /**
     * 팬노트 이미지 업로드
     * @param image 업로드할 이미지 파일
     * @param folderName 팬노트 폴더명 (yyyy-MM-dd-uuid)
     * @param pageNumber 페이지 번호 (0: 커버, 1~: 페이지)
     */
    fun uploadImage(
        image: MultipartFile,
        folderName: String,
        pageNumber: Int
    ): ImageUploadResult {
        val imageBytes = image.bytes
        val dimensions = extractImageDimensions(imageBytes)

        val fileName = generateFileName(image.originalFilename, pageNumber)
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

        val url = s3Client.utilities()
            .getUrl { it.bucket(s3Properties.bucketName).key(key) }
            .toString()

        return ImageUploadResult(
            url = url,
            width = dimensions.first,
            height = dimensions.second
        )
    }

    /**
     * 파일명 생성
     * 형식: yyyyMMdd-{pageNumber}.{extension}
     */
    private fun generateFileName(originalFilename: String?, pageNumber: Int): String {
        val extension = getFileExtension(originalFilename)
        val date = LocalDate.now().format(FILENAME_DATE_FORMATTER)
        return "$date-$pageNumber.$extension"
    }

    private fun getFileExtension(filename: String?): String {
        return filename?.substringAfterLast(".", "")?.lowercase() ?: ""
    }

    private fun extractImageDimensions(imageBytes: ByteArray): Pair<Int, Int> {
        return try {
            val bufferedImage: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
                ?: throw ImageException.imageUnreadable()

            Pair(bufferedImage.width, bufferedImage.height)
        } catch (e: ImageException) {
            throw e
        } catch (e: Exception) {
            throw ImageException.imageSizeExtractionFailed()
        }
    }
}
