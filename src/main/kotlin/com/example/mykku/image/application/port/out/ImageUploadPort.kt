package com.example.mykku.image.application.port.out

import org.springframework.web.multipart.MultipartFile

data class ImageUploadResultData(
    val originalFilename: String,
    val storedFilename: String,
    val imageUrl: String,
    val fileSize: Long
)

data class FanNoteImagesUploadResultData(
    val coverImage: ImageUploadResultData?,
    val pageImages: List<ImageUploadResultData>
)

interface ImageUploadPort {
    fun uploadImages(images: List<MultipartFile>): List<ImageUploadResultData>
    fun uploadImage(image: MultipartFile): ImageUploadResultData
    fun uploadFanNoteImages(
        coverImage: MultipartFile?,
        pageImages: List<MultipartFile>?
    ): FanNoteImagesUploadResultData
}
