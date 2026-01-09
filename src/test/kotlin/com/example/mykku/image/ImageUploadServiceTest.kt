package com.example.mykku.image

import com.example.mykku.BaseServiceTest
import com.example.mykku.config.S3Properties
import com.example.mykku.image.exception.ImageErrorCode
import com.example.mykku.image.exception.ImageException
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.function.Consumer
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Utilities
import software.amazon.awssdk.services.s3.model.GetUrlRequest.Builder
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class ImageUploadServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var s3Properties: S3Properties

    @InjectMocks
    private lateinit var imageUploadService: S3ImageUploadService

    @Test
    fun `uploadImage - 이미지를 정상적으로 업로드한다`() {
        // given
        val imageFile = mock<MultipartFile> {
            on { isEmpty } doReturn false
            on { size } doReturn (1024 * 1024) // 1MB
            on { originalFilename } doReturn "test.jpg"
            on { contentType } doReturn "image/jpeg"
            on { bytes } doReturn createTestImageBytes()
        }

        val s3Utilities = mock<S3Utilities>()
        val url = URL("https://bucket.s3.amazonaws.com/feed-images/test.jpg")

        whenever(s3Properties.bucketName).thenReturn("test-bucket")
        whenever(s3Client.utilities()).thenReturn(s3Utilities)
        doReturn(url).whenever(s3Utilities)
            .getUrl(any<Consumer<Builder>>())
        whenever(
            s3Client.putObject(
                any<PutObjectRequest>(),
                any<RequestBody>()
            )
        ).thenReturn(mock())

        // when
        val result = imageUploadService.uploadImage(imageFile)

        // then
        assertNotNull(result)
        assertEquals(url.toString(), result.url)
        assertEquals(100, result.width)
        assertEquals(100, result.height)
        verify(s3Client).putObject(
            any<PutObjectRequest>(),
            any<RequestBody>()
        )
        verify(s3Client).utilities()
    }

    @Test
    fun `uploadImage - 빈 파일이면 예외가 발생한다`() {
        // given
        val imageFile = mock<MultipartFile> {
            on { isEmpty } doReturn true
        }

        // when & then
        val exception = assertThrows<ImageException> {
            imageUploadService.uploadImage(imageFile)
        }
        assertEquals(ImageErrorCode.IMAGE_FILE_EMPTY, exception.errorCode)
    }

    @Test
    fun `uploadImage - 파일 크기가 너무 크면 예외가 발생한다`() {
        // given
        val imageFile = mock<MultipartFile> {
            on { isEmpty } doReturn false
            on { size } doReturn (11 * 1024 * 1024) // 11MB
        }

        // when & then
        val exception = assertThrows<ImageException> {
            imageUploadService.uploadImage(imageFile)
        }
        assertEquals(ImageErrorCode.IMAGE_FILE_TOO_LARGE, exception.errorCode)
    }

    @Test
    fun `uploadImage - 지원하지 않는 파일 형식이면 예외가 발생한다`() {
        // given
        val imageFile = mock<MultipartFile> {
            on { isEmpty } doReturn false
            on { size } doReturn 1024
            on { originalFilename } doReturn "test.txt"
        }

        // when & then
        val exception = assertThrows<ImageException> {
            imageUploadService.uploadImage(imageFile)
        }
        assertEquals(ImageErrorCode.IMAGE_INVALID_FORMAT, exception.errorCode)
    }

    @Test
    fun `uploadImages - 여러 이미지를 업로드한다`() {
        // given
        val imageFile1 = mock<MultipartFile> {
            on { isEmpty } doReturn false
            on { size } doReturn 1024
            on { originalFilename } doReturn "test1.jpg"
            on { contentType } doReturn "image/jpeg"
            on { bytes } doReturn createTestImageBytes()
        }
        val imageFile2 = mock<MultipartFile> {
            on { isEmpty } doReturn false
            on { size } doReturn 1024
            on { originalFilename } doReturn "test2.png"
            on { contentType } doReturn "image/png"
            on { bytes } doReturn createTestImageBytes()
        }

        val s3Utilities = mock<S3Utilities>()
        val url1 = URL("https://bucket.s3.amazonaws.com/feed-images/test1.jpg")
        val url2 = URL("https://bucket.s3.amazonaws.com/feed-images/test2.png")

        whenever(s3Properties.bucketName).thenReturn("test-bucket")
        whenever(s3Client.utilities()).thenReturn(s3Utilities)
        doReturn(url1, url2).whenever(s3Utilities)
            .getUrl(any<Consumer<Builder>>())
        whenever(
            s3Client.putObject(
                any<PutObjectRequest>(),
                any<RequestBody>()
            )
        ).thenReturn(mock())

        // when
        val result = imageUploadService.uploadImages(listOf(imageFile1, imageFile2))

        // then
        assertEquals(2, result.size)
        assertEquals(url1.toString(), result[0].url)
        assertEquals(url2.toString(), result[1].url)
    }

    private fun createTestImageBytes(): ByteArray {
        val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        return baos.toByteArray()
    }
}
