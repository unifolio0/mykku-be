package com.example.mykku.image

import com.example.mykku.image.dto.FanNoteImagesUploadResult
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.image.exception.ImageException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@ConditionalOnProperty(name = ["aws.s3.enabled"], havingValue = "false", matchIfMissing = true)
class NoOpImageUploadService : ImageUploadService {

    override fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult> {
        throw ImageException.imageUploadServiceUnavailable()
    }

    override fun uploadImage(image: MultipartFile): ImageUploadResult {
        throw ImageException.imageUploadServiceUnavailable()
    }

    override fun uploadFanNoteImages(
        coverImage: MultipartFile?,
        pageImages: List<MultipartFile>?
    ): FanNoteImagesUploadResult {
        throw ImageException.imageUploadServiceUnavailable()
    }
}
