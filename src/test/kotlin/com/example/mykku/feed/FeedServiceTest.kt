package com.example.mykku.feed

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.domain.Board
import com.example.mykku.board.tool.BoardReader
import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestParticipationWriter
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.contest.tool.ContestWinnerWriter
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.UpdateFeedRequest
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.tool.FeedCommentWriter
import com.example.mykku.feed.tool.FeedDtoConverter
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.feed.tool.FeedWriter
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.image.exception.ImageErrorCode
import com.example.mykku.image.exception.ImageException
import com.example.mykku.like.tool.LikeFeedCommentWriter
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.like.tool.LikeFeedWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveFeedReader
import com.example.mykku.scrap.tool.SaveFeedWriter
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.multipart.MultipartFile

class FeedServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var feedReader: FeedReader

    @Mock
    private lateinit var feedWriter: FeedWriter

    @Mock
    private lateinit var feedDtoConverter: FeedDtoConverter

    @Mock
    private lateinit var boardReader: BoardReader

    @Mock
    private lateinit var likeFeedReader: LikeFeedReader

    @Mock
    private lateinit var saveFeedReader: SaveFeedReader

    @Mock
    private lateinit var imageUploadService: ImageUploadService

    @Mock
    private lateinit var contestReader: ContestReader

    @Mock
    private lateinit var contestParticipationWriter: ContestParticipationWriter

    @Mock
    private lateinit var contestParticipationReader: ContestParticipationReader

    @Mock
    private lateinit var feedCommentWriter: FeedCommentWriter

    @Mock
    private lateinit var likeFeedWriter: LikeFeedWriter

    @Mock
    private lateinit var likeFeedCommentWriter: LikeFeedCommentWriter

    @Mock
    private lateinit var saveFeedWriter: SaveFeedWriter

    @Mock
    private lateinit var contestWinnerWriter: ContestWinnerWriter

    @InjectMocks
    private lateinit var feedService: FeedService

    private val member = createTestMember(id = "member1", nickname = "test", email = "test@test.com")
    private val board = createTestBoard(id = 1L, title = "보드1", logo = "https://example.com/logo.png")

    private fun createTestFeed(
        id: Long? = null,
        title: String = "테스트 피드",
        content: String = "테스트 내용",
        board: Board = this.board,
        member: Member = this.member
    ): Feed {
        val feed = Feed(
            id = id,
            title = title,
            content = content,
            board = board,
            member = member
        )
        initializeBaseEntityFieldsFromSuperclass(feed)
        return feed
    }

    @Test
    fun `createFeed - 이미지 없이 피드를 생성한다`() {
        // given
        val request = CreateFeedRequest(
            title = "새 피드",
            content = "피드 내용",
            boardId = 1L,
            images = emptyList(),
            tags = emptyList()
        )

        val feed = createTestFeed(
            id = 1L,
            title = request.title,
            content = request.content,
            board = board,
            member = member
        )

        val feedImages = emptyList<FeedImage>()
        val feedTags = emptyList<FeedTag>()

        whenever(boardReader.getBoardById(1L)).thenReturn(board)
        whenever(
            feedWriter.createFeed(
                title = request.title,
                content = request.content,
                board = board,
                member = member,
                imageResults = emptyList(),
                tagTitles = emptyList()
            )
        ).thenReturn(Triple(feed, feedImages, feedTags))

        // when
        val result = feedService.createFeed(request, member)

        // then
        assertEquals(1L, result.id)
        assertEquals("새 피드", result.title)
        assertEquals("피드 내용", result.content)
        assertEquals(1L, result.boardId)
        assertEquals("보드1", result.boardTitle)
        assertEquals("member1", result.authorId)
        assertEquals("test", result.authorNickname)
        assertTrue(result.images.isEmpty())
        assertTrue(result.tags.isEmpty())
    }

    @Test
    fun `createFeed - 이미지와 함께 피드를 생성한다`() {
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
            id = 1L,
            title = request.title,
            content = request.content,
            board = board,
            member = member
        )

        val feedImages = listOf(
            FeedImage(
                id = 1L,
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feed = feed
            )
        )
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
    fun `createFeed - ImageUploadService가 사용 불가능하고 이미지가 있으면 예외가 발생한다`() {
        // given
        val noOpImageUploadService = mock<ImageUploadService>()
        whenever(noOpImageUploadService.uploadImages(any()))
            .thenThrow(ImageException(ImageErrorCode.IMAGE_UPLOAD_SERVICE_UNAVAILABLE))

        val feedServiceWithoutImageUpload = FeedService(
            feedReader = feedReader,
            feedWriter = feedWriter,
            feedDtoConverter = feedDtoConverter,
            boardReader = boardReader,
            likeFeedReader = likeFeedReader,
            saveFeedReader = saveFeedReader,
            imageUploadService = noOpImageUploadService,
            contestReader = contestReader,
            contestParticipationWriter = contestParticipationWriter,
            contestParticipationReader = contestParticipationReader,
            feedCommentWriter = feedCommentWriter,
            likeFeedWriter = likeFeedWriter,
            likeFeedCommentWriter = likeFeedCommentWriter,
            saveFeedWriter = saveFeedWriter,
            contestWinnerWriter = contestWinnerWriter
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
        val exception = assertThrows<ImageException> {
            feedServiceWithoutImageUpload.createFeed(request, member)
        }
        assertEquals(ImageErrorCode.IMAGE_UPLOAD_SERVICE_UNAVAILABLE, exception.errorCode)
    }

    @Test
    fun `getFeedsByBoard - 보드별 피드를 페이지네이션으로 반환한다`() {
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

        // Mock for FeedDtoConverter
        val feedResponse1 = FeedResponse(
            feed1,
            AuthorResponse(member),
            true,
            false,
            emptySet(),
            emptyList(),
            emptyList(),
            emptyList()
        )
        val feedResponse2 = FeedResponse(
            feed2,
            AuthorResponse(member),
            false,
            true,
            emptySet(),
            emptyList(),
            emptyList(),
            emptyList()
        )
        whenever(feedDtoConverter.convertToFeedResponsesBatch(memberId, listOf(feed1, feed2)))
            .thenReturn(listOf(feedResponse1, feedResponse2))

        // when
        val result = feedService.getFeedsByBoard(boardId, memberId, pageable)

        // then
        assertEquals(2, result.feeds.size)
        assertEquals(0, result.currentPage)
        assertEquals(1, result.totalPages)
        assertEquals(2, result.totalElements)
        assertEquals(20, result.size)
        assertFalse(result.hasNext)
        assertFalse(result.hasPrevious)
        assertEquals("Board Feed 1", result.feeds[0].title)
        assertEquals("Board Feed 2", result.feeds[1].title)
    }

    @Test
    fun `getFeedsByBoard - 비로그인 사용자도 보드별 피드를 조회할 수 있다`() {
        // given
        val boardId = 1L
        val memberId: String? = null

        val feed = createTestFeed(
            id = 1L,
            title = "Public Feed",
            content = "Public Content",
            board = board,
            member = member
        )

        val pageable = PageRequest.of(0, 20)
        val feedPage = PageImpl(listOf(feed), pageable, 1)

        whenever(boardReader.getBoardById(boardId)).thenReturn(board)
        whenever(feedReader.getFeedsByBoardWithPagination(board, pageable))
            .thenReturn(feedPage)

        // Mock for FeedDtoConverter - 비로그인 사용자는 isLiked, isSaved가 false
        val feedResponse = FeedResponse(
            feed,
            AuthorResponse(member),
            false,  // isLiked는 false
            false,  // isSaved는 false
            emptySet(),
            emptyList(),
            emptyList(),
            emptyList()
        )
        whenever(feedDtoConverter.convertToFeedResponsesBatch("", listOf(feed)))
            .thenReturn(listOf(feedResponse))

        // when
        val result = feedService.getFeedsByBoard(boardId, memberId, pageable)

        // then
        assertEquals(1, result.feeds.size)
        assertEquals("Public Feed", result.feeds[0].title)
        assertFalse(result.feeds[0].isLiked)
        assertFalse(result.feeds[0].isSaved)
    }

    @Test
    fun `getFeedDetail - 로그인한 사용자가 피드 상세를 조회한다`() {
        // given
        val feedId = 1L
        val memberId = "member1"

        val feed = createTestFeed(
            id = feedId,
            title = "Detail Feed",
            content = "Detail Content",
            board = board,
            member = member
        )

        val feedImage = FeedImage(
            id = 1L,
            url = "https://s3.amazonaws.com/detail.jpg",
            width = 1920,
            height = 1080,
            feed = feed
        )
        val feedTag = FeedTag(title = "태그1", feed = feed)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(likeFeedReader.isLiked(memberId, feed)).thenReturn(true)
        whenever(saveFeedReader.isSaved(memberId, feed)).thenReturn(false)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(listOf(feedImage))
        whenever(feedReader.getFeedTagsByFeed(feed)).thenReturn(listOf(feedTag))
        whenever(feedReader.getContestTagsByTitles(listOf("태그1"))).thenReturn(emptyList())

        // when
        val result = feedService.getFeedDetail(feedId, memberId)

        // then
        assertEquals(feedId, result.id)
        assertEquals("Detail Feed", result.title)
        assertEquals("Detail Content", result.content)
        assertTrue(result.isLiked)
        assertFalse(result.isSaved)
        assertEquals(1, result.images.size)
        assertEquals(1, result.tags.size)
        assertEquals(0, result.tags.count { it.isContest })
    }

    @Test
    fun `getFeedDetail - 비로그인 사용자도 피드 상세를 조회할 수 있다`() {
        // given
        val feedId = 1L
        val memberId: String? = null

        val feed = createTestFeed(
            id = feedId,
            title = "Public Detail Feed",
            content = "Public Detail Content",
            board = board,
            member = member
        )

        val feedImage = FeedImage(
            id = 1L,
            url = "https://s3.amazonaws.com/public.jpg",
            width = 1920,
            height = 1080,
            feed = feed
        )
        val feedTag = FeedTag(title = "공개태그", feed = feed)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(listOf(feedImage))
        whenever(feedReader.getFeedTagsByFeed(feed)).thenReturn(listOf(feedTag))
        whenever(feedReader.getContestTagsByTitles(listOf("공개태그"))).thenReturn(emptyList())

        // when
        val result = feedService.getFeedDetail(feedId, memberId)

        // then
        assertEquals(feedId, result.id)
        assertEquals("Public Detail Feed", result.title)
        assertFalse(result.isLiked)  // 비로그인 사용자는 좋아요 상태가 false
        assertFalse(result.isSaved)  // 비로그인 사용자는 저장 상태가 false
        assertEquals(1, result.images.size)
        assertEquals(1, result.tags.size)
    }

    @Test
    fun `deleteFeed - 작성자가 피드를 삭제한다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getCommentIdsByFeed(feed)).thenReturn(listOf(1L, 2L))
        whenever(contestParticipationReader.getParticipationsByFeed(feed)).thenReturn(emptyList())

        // when
        feedService.deleteFeed(feedId, member)

        // then
        verify(likeFeedCommentWriter).deleteAllByFeedCommentIds(listOf(1L, 2L))
        verify(feedCommentWriter).deleteAllByFeed(feed)
        verify(likeFeedWriter).deleteAllByFeedId(feedId)
        verify(saveFeedWriter).deleteAllByFeed(feed)
        verify(contestParticipationReader).getParticipationsByFeed(feed)
        verify(contestWinnerWriter).deleteAllByParticipations(emptyList())
        verify(contestParticipationWriter).deleteAllByFeed(feed)
        verify(feedWriter).deleteFeed(feed)
    }

    @Test
    fun `deleteFeed - 작성자가 아닌 경우 예외 발생`() {
        // given
        val feedId = 1L
        val otherMember = createTestMember(id = "other-member-id", nickname = "other", email = "other@test.com")
        val feed = createTestFeed(id = feedId, member = otherMember)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)

        // when & then
        val exception = assertThrows<FeedException> {
            feedService.deleteFeed(feedId, member)
        }
        assertEquals(FeedErrorCode.FEED_FORBIDDEN_ACCESS, exception.errorCode)

        verify(feedWriter, never()).deleteFeed(any())
    }

    @Test
    fun `deleteFeed - 피드가 존재하지 않으면 예외 발생`() {
        // given
        val feedId = 999L

        whenever(feedReader.getFeedById(feedId)).thenThrow(FeedException.feedNotFound())

        // when & then
        val exception = assertThrows<FeedException> {
            feedService.deleteFeed(feedId, member)
        }
        assertEquals(FeedErrorCode.FEED_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `deleteFeed - 댓글이 없는 피드도 정상 삭제된다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getCommentIdsByFeed(feed)).thenReturn(emptyList())
        whenever(contestParticipationReader.getParticipationsByFeed(feed)).thenReturn(emptyList())

        // when
        feedService.deleteFeed(feedId, member)

        // then
        verify(likeFeedCommentWriter).deleteAllByFeedCommentIds(emptyList())
        verify(feedCommentWriter).deleteAllByFeed(feed)
        verify(likeFeedWriter).deleteAllByFeedId(feedId)
        verify(saveFeedWriter).deleteAllByFeed(feed)
        verify(contestParticipationReader).getParticipationsByFeed(feed)
        verify(contestWinnerWriter).deleteAllByParticipations(emptyList())
        verify(contestParticipationWriter).deleteAllByFeed(feed)
        verify(feedWriter).deleteFeed(feed)
    }

    @Test
    fun `updateFeed - 피드를 성공적으로 수정한다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)
        val request = UpdateFeedRequest(
            title = "수정된 제목",
            content = "수정된 내용",
            boardId = null,
            tags = listOf("새태그"),
            deleteImageIds = emptyList(),
            newImages = emptyList()
        )

        val updatedFeed = createTestFeed(id = feedId, title = "수정된 제목", content = "수정된 내용", member = member)
        val updatedTags = listOf(FeedTag(title = "새태그", feed = updatedFeed))

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(emptyList())
        whenever(
            feedWriter.updateFeed(
                feed = feed,
                title = "수정된 제목",
                content = "수정된 내용",
                board = null,
                deleteImageIds = emptyList(),
                newImageResults = emptyList(),
                tagTitles = listOf("새태그")
            )
        ).thenReturn(Triple(updatedFeed, emptyList(), updatedTags))
        whenever(feedReader.getContestTagsByTitles(listOf("새태그"))).thenReturn(emptyList())
        whenever(likeFeedReader.isLiked(member.id, updatedFeed)).thenReturn(false)
        whenever(saveFeedReader.isSaved(member.id, updatedFeed)).thenReturn(false)

        // when
        val result = feedService.updateFeed(feedId, request, member)

        // then
        assertEquals("수정된 제목", result.title)
        assertEquals("수정된 내용", result.content)
        assertEquals(1, result.tags.size)
        assertEquals("새태그", result.tags[0].title)
    }

    @Test
    fun `updateFeed - 작성자가 아니면 예외 발생`() {
        // given
        val feedId = 1L
        val otherMember = createTestMember(id = "other-member-id", nickname = "other", email = "other@test.com")
        val feed = createTestFeed(id = feedId, member = otherMember)
        val request = UpdateFeedRequest(
            title = "수정된 제목",
            content = null,
            boardId = null,
            tags = null,
            deleteImageIds = emptyList(),
            newImages = emptyList()
        )

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)

        // when & then
        val exception = assertThrows<FeedException> {
            feedService.updateFeed(feedId, request, member)
        }
        assertEquals(FeedErrorCode.FEED_FORBIDDEN_ACCESS, exception.errorCode)
    }

    @Test
    fun `updateFeed - 최종 이미지 개수가 초과하면 예외 발생`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)
        val existingImages = List(10) {
            FeedImage(id = it.toLong(), url = "image$it.jpg", width = 100, height = 100, feed = feed)
        }
        val newImage = mock<MultipartFile>()
        val request = UpdateFeedRequest(
            title = null,
            content = null,
            boardId = null,
            tags = null,
            deleteImageIds = emptyList(),
            newImages = listOf(newImage)
        )

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(existingImages)

        // when & then
        val exception = assertThrows<FeedException> {
            feedService.updateFeed(feedId, request, member)
        }
        assertEquals(FeedErrorCode.FEED_IMAGE_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `updateFeed - 이미지 삭제와 추가가 정상 동작한다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)
        val existingImage = FeedImage(id = 1L, url = "old.jpg", width = 100, height = 100, feed = feed)
        val newImageFile = mock<MultipartFile>()
        val request = UpdateFeedRequest(
            title = null,
            content = null,
            boardId = null,
            tags = null,
            deleteImageIds = listOf(1L),
            newImages = listOf(newImageFile)
        )

        val newImageResult = ImageUploadResult(url = "new.jpg", width = 200, height = 200)
        val newFeedImage = FeedImage(id = 2L, url = "new.jpg", width = 200, height = 200, feed = feed)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(listOf(existingImage))
        whenever(imageUploadService.uploadImages(listOf(newImageFile))).thenReturn(listOf(newImageResult))
        whenever(
            feedWriter.updateFeed(
                feed = feed,
                title = null,
                content = null,
                board = null,
                deleteImageIds = listOf(1L),
                newImageResults = listOf(newImageResult),
                tagTitles = null
            )
        ).thenReturn(Triple(feed, listOf(newFeedImage), emptyList()))
        whenever(feedReader.getContestTagsByTitles(emptyList())).thenReturn(emptyList())
        whenever(likeFeedReader.isLiked(member.id, feed)).thenReturn(false)
        whenever(saveFeedReader.isSaved(member.id, feed)).thenReturn(false)

        // when
        val result = feedService.updateFeed(feedId, request, member)

        // then
        assertEquals(1, result.images.size)
        assertEquals("new.jpg", result.images[0].url)
    }

    @Test
    fun `updateFeed - 게시판을 변경한다`() {
        // given
        val feedId = 1L
        val feed = createTestFeed(id = feedId, member = member)
        val newBoard = createTestBoard(id = 2L, title = "새 게시판", logo = "https://example.com/new-logo.png")
        val request = UpdateFeedRequest(
            title = null,
            content = null,
            boardId = 2L,
            tags = null,
            deleteImageIds = emptyList(),
            newImages = emptyList()
        )

        val updatedFeed = createTestFeed(id = feedId, board = newBoard, member = member)

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(feedReader.getFeedImagesByFeed(feed)).thenReturn(emptyList())
        whenever(boardReader.getBoardById(2L)).thenReturn(newBoard)
        whenever(
            feedWriter.updateFeed(
                feed = feed,
                title = null,
                content = null,
                board = newBoard,
                deleteImageIds = emptyList(),
                newImageResults = emptyList(),
                tagTitles = null
            )
        ).thenReturn(Triple(updatedFeed, emptyList(), emptyList()))
        whenever(feedReader.getContestTagsByTitles(emptyList())).thenReturn(emptyList())
        whenever(likeFeedReader.isLiked(member.id, updatedFeed)).thenReturn(false)
        whenever(saveFeedReader.isSaved(member.id, updatedFeed)).thenReturn(false)

        // when
        val result = feedService.updateFeed(feedId, request, member)

        // then
        assertEquals(2L, result.boardId)
        assertEquals("새 게시판", result.boardTitle)
    }

    @Test
    fun `getPopularFeedsByBoard - 인기 피드 목록을 반환한다`() {
        // given
        val boardId = 1L

        val feed1 = createTestFeed(
            id = 1L,
            title = "인기 피드 1",
            content = "Content 1",
            board = board,
            member = member
        )
        val feed2 = createTestFeed(
            id = 2L,
            title = "인기 피드 2",
            content = "Content 2",
            board = board,
            member = member
        )

        whenever(boardReader.getBoardById(boardId)).thenReturn(board)
        whenever(feedReader.getPopularFeedsByBoard(board)).thenReturn(listOf(feed1, feed2))

        // when
        val result = feedService.getPopularFeedsByBoard(boardId)

        // then
        assertEquals(2, result.feeds.size)
        assertEquals(1L, result.feeds[0].id)
        assertEquals(1, result.feeds[0].rank)
        assertEquals("인기 피드 1", result.feeds[0].title)
        assertEquals("Content 1", result.feeds[0].content)
        assertEquals(2L, result.feeds[1].id)
        assertEquals(2, result.feeds[1].rank)
        assertEquals("인기 피드 2", result.feeds[1].title)
    }

    @Test
    fun `getPopularFeedsByBoard - 인기 피드가 없으면 빈 목록을 반환한다`() {
        // given
        val boardId = 1L

        whenever(boardReader.getBoardById(boardId)).thenReturn(board)
        whenever(feedReader.getPopularFeedsByBoard(board)).thenReturn(emptyList())

        // when
        val result = feedService.getPopularFeedsByBoard(boardId)

        // then
        assertTrue(result.feeds.isEmpty())
    }
}
