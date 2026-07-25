package com.example.mykku.image

import com.example.mykku.config.S3Properties
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.mock.web.MockMultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@DisplayName("S3ImageUploadService 테스트")
class S3ImageUploadServiceTest {

    private val s3Client: S3Client = mock()
    private val s3Properties = S3Properties(
        region = "ap-northeast-2",
        bucketName = "test-bucket",
        cloudfrontDomain = "cdn.test.com"
    )
    private val s3ImageUploadService = S3ImageUploadService(s3Client, s3Properties)

    @Test
    @DisplayName("게시판 로고는 board-images 경로에 업로드된다")
    fun `단일 업로드 - board-images 프리픽스`() {
        val result = s3ImageUploadService.uploadImage(pngFile("logo.png"), "board-images")

        val key = capturedKeys().single()
        assertThat(key).startsWith("board-images/")
        assertThat(key).endsWith(".png")
        assertThat(result.url).isEqualTo("https://cdn.test.com/$key")
    }

    @Test
    @DisplayName("피드 이미지는 feed-images 경로에 업로드된다")
    fun `다중 업로드 - feed-images 프리픽스`() {
        s3ImageUploadService.uploadImages(
            listOf(pngFile("first.png"), pngFile("second.png")),
            "feed-images"
        )

        assertThat(capturedKeys()).hasSize(2).allSatisfy {
            assertThat(it).startsWith("feed-images/")
        }
    }

    private fun capturedKeys(): List<String> {
        val captor = argumentCaptor<PutObjectRequest>()
        verify(s3Client, atLeastOnce()).putObject(captor.capture(), any<RequestBody>())
        return captor.allValues.map { it.key() }
    }

    private fun pngFile(fileName: String): MockMultipartFile {
        val output = ByteArrayOutputStream()
        ImageIO.write(BufferedImage(10, 20, BufferedImage.TYPE_INT_RGB), "png", output)
        return MockMultipartFile("image", fileName, "image/png", output.toByteArray())
    }
}
