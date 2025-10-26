package com.example.mykku.image

import com.example.mykku.image.dto.FanNoteImagesUploadResult
import com.example.mykku.image.dto.ImageUploadResult
import org.springframework.web.multipart.MultipartFile

interface ImageUploadService {
    fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult>
    fun uploadImage(image: MultipartFile): ImageUploadResult
    fun uploadFanNoteImages(
        coverImage: MultipartFile?,
        pageImages: List<MultipartFile>?
    ): FanNoteImagesUploadResult
}
