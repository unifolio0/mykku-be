package com.example.mykku.config

import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.FanNoteImagesUploadResult
import com.example.mykku.image.dto.ImageUploadResult
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.multipart.MultipartFile

@TestConfiguration
class TestImageUploadConfig {

    @Bean
    @Primary
    fun testImageUploadService(): ImageUploadService {
        return object : ImageUploadService {
            override fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult> {
                return images.map {
                    ImageUploadResult(
                        url = "https://test-bucket.s3.amazonaws.com/test-image-${System.currentTimeMillis()}.jpg",
                        width = 800,
                        height = 600
                    )
                }
            }

            override fun uploadImage(image: MultipartFile): ImageUploadResult {
                return ImageUploadResult(
                    url = "https://test-bucket.s3.amazonaws.com/test-image-${System.currentTimeMillis()}.jpg",
                    width = 800,
                    height = 600
                )
            }

            override fun uploadFanNoteImages(
                coverImage: MultipartFile?,
                pageImages: List<MultipartFile>?
            ): FanNoteImagesUploadResult {
                val coverImageUrl = coverImage?.let {
                    "https://test-bucket.s3.amazonaws.com/cover-${System.currentTimeMillis()}.jpg"
                }
                val pageImageUrls = pageImages?.map {
                    "https://test-bucket.s3.amazonaws.com/page-${System.currentTimeMillis()}.jpg"
                } ?: emptyList()

                return FanNoteImagesUploadResult(
                    coverImageUrl = coverImageUrl,
                    pageImageUrls = pageImageUrls
                )
            }

            override fun uploadEntityImages(
                thumbnailImage: MultipartFile,
                images: List<MultipartFile>?,
                pathPrefix: String
            ): com.example.mykku.image.dto.EntityImagesUploadResult {
                val thumbnailUrl = "https://test-bucket.s3.amazonaws.com/$pathPrefix/thumbnail-${System.currentTimeMillis()}.jpg"
                val imageUrls = images?.mapIndexed { index, _ ->
                    "https://test-bucket.s3.amazonaws.com/$pathPrefix/image-$index-${System.currentTimeMillis()}.jpg"
                } ?: emptyList()

                return com.example.mykku.image.dto.EntityImagesUploadResult(
                    thumbnailUrl = thumbnailUrl,
                    imageUrls = imageUrls
                )
            }

            override fun delete(url: String) {
            }
        }
    }
}
