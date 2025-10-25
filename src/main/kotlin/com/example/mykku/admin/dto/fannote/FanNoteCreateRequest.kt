package com.example.mykku.admin.dto.fannote

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class FanNoteCreateRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,

    val subtitle: String?,

    val content: String?,

    @field:NotNull(message = "제작일은 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val productionDate: LocalDate,

    val coverImage: MultipartFile?,

    val pageImages: List<MultipartFile>?
)
