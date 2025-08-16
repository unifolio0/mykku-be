package com.example.mykku.image.service

import com.example.mykku.config.S3Properties
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
        
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucketName)
            .key(key)
            .contentType(image.contentType)
            .contentLength(image.size)
            .build()
        
        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromBytes(imageBytes)
        )
        
        val url = "https://${s3Properties.bucketName}.s3.${s3Properties.region}.amazonaws.com/$key"
        return ImageUploadResult(
            url = url,
            width = dimensions.first,
            height = dimensions.second
        )
    }
    
    private fun validateImage(image: MultipartFile) {
        if (image.isEmpty) {
            throw IllegalArgumentException("이미지 파일이 비어있습니다")
        }
        
        if (image.size > MAX_FILE_SIZE) {
            throw IllegalArgumentException("이미지 파일 크기는 10MB 이하여야 합니다")
        }
        
        val extension = getFileExtension(image.originalFilename)
        if (extension !in ALLOWED_EXTENSIONS) {
            throw IllegalArgumentException("지원하지 않는 이미지 형식입니다. (지원 형식: ${ALLOWED_EXTENSIONS.joinToString(", ")})")
        }
    }
    
    private fun generateFileName(originalFilename: String?): String {
        val extension = getFileExtension(originalFilename)
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "${timestamp}_${uuid}.$extension"
    }
    
    private fun getFileExtension(filename: String?): String {
        return filename?.substringAfterLast(".", "")?.lowercase() ?: ""
    }
    
    private fun extractImageDimensions(imageBytes: ByteArray): Pair<Int, Int> {
        return try {
            val bufferedImage: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
                ?: throw IllegalArgumentException("이미지를 읽을 수 없습니다")
            
            Pair(bufferedImage.width, bufferedImage.height)
        } catch (e: Exception) {
            throw IllegalArgumentException("이미지 크기를 추출할 수 없습니다: ${e.message}")
        }
    }
}