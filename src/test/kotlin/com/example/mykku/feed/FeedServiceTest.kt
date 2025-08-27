package com.example.mykku.feed

import com.example.mykku.board.domain.Board
import com.example.mykku.board.tool.BoardReader
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.repository.EventTagRepository
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedTagRepository
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.feed.tool.FeedWriter
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class FeedServiceTest {

    @Mock
    private lateinit var feedReader: FeedReader

    @Mock
    private lateinit var feedWriter: FeedWriter

    @Mock
    private lateinit var boardReader: BoardReader

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var likeFeedReader: LikeFeedReader

    @Mock
    private lateinit var saveFeedReader: SaveFeedReader

    @Mock
    private lateinit var eventTagRepository: EventTagRepository

    @Mock
    private lateinit var feedCommentRepository: FeedCommentRepository

    @Mock
    private lateinit var feedImageRepository: FeedImageRepository

    @Mock
    private lateinit var feedTagRepository: FeedTagRepository

    @Mock
    private lateinit var imageUploadService: ImageUploadService

    @InjectMocks
    private lateinit var feedService: FeedService

    private val member = Member(
        id = "member1",
        nickname = "testUser",
        role = "USER",
        profileImage = "",
        provider = SocialProvider.GOOGLE,
        socialId = "123",
        email = "test@test.com"
    )

    private val board = Board(id = 1L, title = "테스트 보드", logo = "")
    
    private fun createTestFeed(
        id: Long = 1L,
        title: String,
        content: String,
        board: Board,
        member: Member
    ): Feed {
        val feed = Feed(
            id = id,
            title = title,
            content = content,
            board = board,
            member = member
        )
        initializeBaseEntityFields(feed)
        return feed
    }
    
    private fun initializeBaseEntityFields(entity: Any) {
        val now = LocalDateTime.now()
        val createdAtField = entity::class.java.superclass.getDeclaredField("createdAt")
        createdAtField.isAccessible = true
        createdAtField.set(entity, now)
        
        val updatedAtField = entity::class.java.superclass.getDeclaredField("updatedAt")
        updatedAtField.isAccessible = true
        updatedAtField.set(entity, now)
    }

    @Test
    fun `createFeed - 이미지가 없는 피드를 생성한다`() {
        // given
        val request = CreateFeedRequest(
            title = "새 피드",
            content = "피드 내용",
            boardId = 1L,
            images = emptyList(),
            tags = listOf("태그1", "태그2")
        )

        val feed = createTestFeed(
            title = request.title,
            content = request.content,
            board = board,
            member = member
        )

        val feedImages = emptyList<FeedImage>()
        val feedTags = listOf(
            FeedTag(title = "태그1", feed = feed),
            FeedTag(title = "태그2", feed = feed)
        )

        whenever(boardReader.getBoardById(1L)).thenReturn(board)
        whenever(
            feedWriter.createFeed(
                title = request.title,
                content = request.content,
                board = board,
                member = member,
                imageResults = emptyList(),
                tagTitles = request.tags
            )
        ).thenReturn(Triple(feed, feedImages, feedTags))

        // when
        val result = feedService.createFeed(request, member)

        // then
        assertEquals(feed.title, result.title)
        assertEquals(feed.content, result.content)
        assertEquals(member.id, result.authorId)
        assertEquals(member.nickname, result.authorNickname)
        assertEquals(0, result.images.size)
        assertEquals(2, result.tags.size)
    }

    @Test
    fun `createFeed - 이미지가 있는 피드를 생성한다`() {
        // given
        val imageFile = mock<MultipartFile>()
        val request = CreateFeedRequest(
            title = "새 피드",
            content = "피드 내용",
            boardId = 1L,
            images = listOf(imageFile),
            tags = emptyList()
        )

        val imageResult = ImageUploadResult(
            url = "https://s3.amazonaws.com/image.jpg",
            width = 1920,
            height = 1080
        )

        val feed = createTestFeed(
            title = request.title,
            content = request.content,
            board = board,
            member = member
        )

        val feedImage = FeedImage(
            url = imageResult.url,
            width = imageResult.width,
            height = imageResult.height,
            feed = feed
        )
        val feedImages = listOf(feedImage)
        val feedTags = emptyList<FeedTag>()

        whenever(boardReader.getBoardById(1L)).thenReturn(board)
        whenever(imageUploadService.uploadImages(listOf(imageFile))).thenReturn(listOf(imageResult))
        whenever(
            feedWriter.createFeed(
                title = request.title,
                content = request.content,
                board = board,
                member = member,
                imageResults = listOf(imageResult),
                tagTitles = emptyList()
            )
        ).thenReturn(Triple(feed, feedImages, feedTags))

        // when
        val result = feedService.createFeed(request, member)

        // then
        assertEquals(1, result.images.size)
        assertEquals(imageResult.url, result.images[0].url)
        assertEquals(imageResult.width, result.images[0].width)
        assertEquals(imageResult.height, result.images[0].height)
    }

    @Test
    fun `createFeed - ImageUploadService가 null이고 이미지가 있으면 예외가 발생한다`() {
        // given
        val feedServiceWithoutImageUpload = FeedService(
            feedReader = feedReader,
            feedWriter = feedWriter,
            boardReader = boardReader,
            memberReader = memberReader,
            likeFeedReader = likeFeedReader,
            saveFeedReader = saveFeedReader,
            eventTagRepository = eventTagRepository,
            feedCommentRepository = feedCommentRepository,
            feedImageRepository = feedImageRepository,
            feedTagRepository = feedTagRepository,
            imageUploadService = null
        )

        val imageFile = mock<MultipartFile>()
        val request = CreateFeedRequest(
            title = "새 피드",
            content = "피드 내용",
            boardId = 1L,
            images = listOf(imageFile),
            tags = emptyList()
        )

        whenever(boardReader.getBoardById(1L)).thenReturn(board)

        // when & then
        val exception = assertThrows<MykkuException> {
            feedServiceWithoutImageUpload.createFeed(request, member)
        }
        assertEquals(ErrorCode.IMAGE_UPLOAD_SERVICE_UNAVAILABLE, exception.errorCode)
    }

    @Test
    fun `getFeeds - 팔로워의 피드 목록을 반환한다`() {
        // given
        val follower = member
        val feed = createTestFeed(
            title = "피드 제목",
            content = "피드 내용",
            board = board,
            member = member
        )

        val feedImage = FeedImage(
            url = "https://s3.amazonaws.com/image.jpg",
            width = 1920,
            height = 1080,
            feed = feed
        )
        val feedTag = FeedTag(title = "태그1", feed = feed)
        val feedComment = FeedComment(
            content = "댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        whenever(memberReader.getFollowerByMemberId("member1")).thenReturn(listOf(follower))
        whenever(feedReader.getFeedsByFollower(listOf(follower))).thenReturn(listOf(feed))
        whenever(likeFeedReader.isLiked("member1", feed)).thenReturn(true)
        whenever(saveFeedReader.isSaved("member1", feed)).thenReturn(false)
        whenever(feedImageRepository.findByFeed(feed)).thenReturn(listOf(feedImage))
        whenever(feedTagRepository.findByFeed(feed)).thenReturn(listOf(feedTag))
        whenever(
            feedCommentRepository.findByFeedAndParentCommentIsNull(
                feed,
                PageRequest.of(0, 1)
            )
        ).thenReturn(PageImpl(listOf(feedComment)))
        whenever(eventTagRepository.findAllByTitleIn(listOf("태그1"))).thenReturn(emptyList())

        // when
        val result = feedService.getFeeds("member1")

        // then
        assertEquals(1, result.feeds.size)
        assertEquals(feed.title, result.feeds[0].title)
        assertEquals(feed.content, result.feeds[0].content)
        assertEquals(true, result.feeds[0].isLiked)
        assertEquals(false, result.feeds[0].isSaved)
        assertEquals(1, result.feeds[0].images.size)
        assertEquals(1, result.feeds[0].tags.size)
    }
}
