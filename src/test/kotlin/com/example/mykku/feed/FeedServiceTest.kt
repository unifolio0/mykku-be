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
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(listOf(feedImage))
        whenever(feedReader.getFeedTagsByFeed(feed)).thenReturn(listOf(feedTag))
        whenever(
            feedReader.getFeedCommentsByFeed(
                feed,
                PageRequest.of(0, 1)
            )
        ).thenReturn(PageImpl(listOf(feedComment)))
        whenever(feedReader.getEventTagsByTitles(listOf("태그1"))).thenReturn(emptyList())

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

    @Test
    fun `getFeedsByMemberWithRecommendations - 팔로우와 추천 사용자의 피드를 페이지네이션으로 반환한다`() {
        // given
        val memberId = "member1"
        val followingMember = Member(
            id = "member2",
            nickname = "following",
            role = "USER",
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = "456",
            email = "following@test.com"
        )
        val recommendedMember = Member(
            id = "member3",
            nickname = "recommend",
            role = "USER",
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = "789",
            email = "recommended@test.com"
        )
        
        val feed1 = createTestFeed(
            id = 1L,
            title = "Following Feed",
            content = "Content from following",
            board = board,
            member = followingMember
        )
        val feed2 = createTestFeed(
            id = 2L,
            title = "Recommended Feed",
            content = "Content from recommended",
            board = board,
            member = recommendedMember
        )
        
        val pageable = PageRequest.of(0, 10)
        val feedPage = PageImpl(listOf(feed1, feed2), pageable, 2)
        
        whenever(memberReader.getFollowerByMemberId(memberId)).thenReturn(listOf(followingMember))
        whenever(memberReader.getRecommendedMembersByCommonFollowers(memberId, 10L))
            .thenReturn(listOf(recommendedMember))
        whenever(feedReader.getFeedsByMembersWithPagination(any(), eq(pageable)))
            .thenReturn(feedPage)
        
        // Mock for getFeedResponse
        whenever(likeFeedReader.isLiked(memberId, feed1)).thenReturn(true)
        whenever(likeFeedReader.isLiked(memberId, feed2)).thenReturn(false)
        whenever(saveFeedReader.isSaved(memberId, feed1)).thenReturn(false)
        whenever(saveFeedReader.isSaved(memberId, feed2)).thenReturn(true)
        whenever(feedReader.getFeedImagesByFeed(any())).thenReturn(emptyList())
        whenever(feedReader.getFeedTagsByFeed(any())).thenReturn(emptyList())
        whenever(feedReader.getFeedCommentsByFeed(any(), any()))
            .thenReturn(PageImpl(emptyList()))
        whenever(feedReader.getEventTagsByTitles(any())).thenReturn(emptyList())
        
        // when
        val result = feedService.getFeedsByMemberWithRecommendations(memberId, pageable, 10L)
        
        // then
        assertEquals(2, result.feeds.size)
        assertEquals(0, result.currentPage)
        assertEquals(1, result.totalPages)
        assertEquals(2, result.totalElements)
        assertEquals(10, result.size)
        assertFalse(result.hasNext)
        assertFalse(result.hasPrevious)
        assertEquals("Following Feed", result.feeds[0].title)
        assertEquals("Recommended Feed", result.feeds[1].title)
    }

    @Test
    fun `getFeedsByBoard - 특정 보드의 피드를 페이지네이션으로 반환한다`() {
        // given
        val boardId = 1L
        val memberId = "member1"
        val feed1 = createTestFeed(
            id = 1L,
            title = "Board Feed 1",
            content = "Content 1",
            board = board,
            member = member
        )
        val feed2 = createTestFeed(
            id = 2L,
            title = "Board Feed 2",
            content = "Content 2",
            board = board,
            member = member
        )
        
        val pageable = PageRequest.of(0, 20)
        val feedPage = PageImpl(listOf(feed1, feed2), pageable, 2)
        
        whenever(boardReader.getBoardById(boardId)).thenReturn(board)
        whenever(feedReader.getFeedsByBoardWithPagination(board, pageable))
            .thenReturn(feedPage)
        
        // Mock for getFeedResponse
        whenever(likeFeedReader.isLiked(eq(memberId), any())).thenReturn(false)
        whenever(saveFeedReader.isSaved(eq(memberId), any())).thenReturn(false)
        whenever(feedReader.getFeedImagesByFeed(any())).thenReturn(emptyList())
        whenever(feedReader.getFeedTagsByFeed(any())).thenReturn(emptyList())
        whenever(feedReader.getFeedCommentsByFeed(any(), any()))
            .thenReturn(PageImpl(emptyList()))
        whenever(feedReader.getEventTagsByTitles(any())).thenReturn(emptyList())
        
        // when
        val result = feedService.getFeedsByBoard(boardId, memberId, pageable)
        
        // then
        assertEquals(2, result.feeds.size)
        assertEquals(0, result.currentPage)
        assertEquals(1, result.totalPages)
        assertEquals(2, result.totalElements)
        assertEquals("Board Feed 1", result.feeds[0].title)
        assertEquals("Board Feed 2", result.feeds[1].title)
    }

    @Test
    fun `getFeedDetail - 피드 상세 정보를 반환한다`() {
        // given
        val feedId = 1L
        val memberId = "member1"
        val feed = createTestFeed(
            id = feedId,
            title = "Detail Feed",
            content = "Detailed content",
            board = board,
            member = member
        )
        
        val feedImage = FeedImage(
            url = "https://example.com/image.jpg",
            width = 1920,
            height = 1080,
            feed = feed
        )
        val feedTag = FeedTag(title = "DetailTag", feed = feed)
        
        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(likeFeedReader.isLiked(memberId, feed)).thenReturn(true)
        whenever(saveFeedReader.isSaved(memberId, feed)).thenReturn(true)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(listOf(feedImage))
        whenever(feedReader.getFeedTagsByFeed(feed)).thenReturn(listOf(feedTag))
        whenever(feedReader.getEventTagsByTitles(listOf("DetailTag")))
            .thenReturn(emptyList())
        
        // when
        val result = feedService.getFeedDetail(feedId, memberId)
        
        // then
        assertEquals(feedId, result.id)
        assertEquals("Detail Feed", result.title)
        assertEquals("Detailed content", result.content)
        assertEquals(board.id, result.boardId)
        assertEquals(board.title, result.boardTitle)
        assertTrue(result.isLiked)
        assertTrue(result.isSaved)
        assertEquals(1, result.images.size)
        assertEquals(1, result.tags.size)
        assertEquals("DetailTag", result.tags[0].title)
        assertFalse(result.tags[0].isEvent)
    }

    @Test
    fun `getFeedDetail - 로그인하지 않은 사용자도 피드 상세 정보를 볼 수 있다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(
            id = feedId,
            title = "Public Feed",
            content = "Public content",
            board = board,
            member = member
        )
        
        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(emptyList())
        whenever(feedReader.getFeedTagsByFeed(feed)).thenReturn(emptyList())
        whenever(feedReader.getEventTagsByTitles(any())).thenReturn(emptyList())
        
        // when
        val result = feedService.getFeedDetail(feedId, null)
        
        // then
        assertEquals(feedId, result.id)
        assertEquals("Public Feed", result.title)
        assertFalse(result.isLiked)
        assertFalse(result.isSaved)
    }
}
