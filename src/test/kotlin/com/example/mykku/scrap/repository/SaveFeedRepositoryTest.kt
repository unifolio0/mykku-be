package com.example.mykku.scrap.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest

@DisplayName("SaveFeedRepository 테스트")
class SaveFeedRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveFeedRepository: SaveFeedRepository

    @Autowired
    private lateinit var folderRepository: FolderRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("피드를 저장하고 조회한다")
    fun `피드를 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "피드 내용",
                board = board,
                member = member
            )
        )

        val saveFeed = SaveFeed(
            member = member,
            feed = feed,
            folder = folder
        )

        // when
        val savedSaveFeed = saveFeedRepository.save(saveFeed)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundSaveFeed = saveFeedRepository.findById(savedSaveFeed.id!!).orElse(null)

        // then
        assertThat(foundSaveFeed).isNotNull
        assertThat(foundSaveFeed.member.id).isEqualTo(member.id)
        assertThat(foundSaveFeed.feed.id).isEqualTo(feed.id)
        assertThat(foundSaveFeed.folder.id).isEqualTo(folder.id)
    }

    @Test
    @DisplayName("회원과 피드로 저장 여부를 확인한다")
    fun `회원과 피드로 저장 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "피드 내용",
                board = board,
                member = member
            )
        )

        saveFeedRepository.save(SaveFeed(member = member, feed = feed, folder = folder))
        testEntityManager.flush()

        // when
        val exists = saveFeedRepository.existsByMemberAndFeed(member, feed)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("회원과 피드 ID 목록으로 저장된 피드를 조회한다")
    fun `회원과 피드 ID 목록으로 저장된 피드를 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        val feed1 = feedRepository.save(Feed(title = "피드1", content = "내용1", board = board, member = member))
        val feed2 = feedRepository.save(Feed(title = "피드2", content = "내용2", board = board, member = member))
        val feed3 = feedRepository.save(Feed(title = "피드3", content = "내용3", board = board, member = member))

        saveFeedRepository.save(SaveFeed(member = member, feed = feed1, folder = folder))
        saveFeedRepository.save(SaveFeed(member = member, feed = feed3, folder = folder))
        testEntityManager.flush()

        // when
        val feedIds = listOf(feed1.id!!, feed2.id!!, feed3.id!!)
        val savedFeeds = saveFeedRepository.findByMemberAndFeedIdIn(member, feedIds)

        // then
        assertThat(savedFeeds).hasSize(2)
        assertThat(savedFeeds.map { it.feed.id }).containsExactlyInAnyOrder(feed1.id, feed3.id)
    }

    @Test
    @DisplayName("회원의 모든 저장된 피드를 페이지네이션으로 조회한다")
    fun `회원의 모든 저장된 피드를 페이지네이션으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        for (i in 1..15) {
            val feed = feedRepository.save(
                Feed(title = "피드$i", content = "내용$i", board = board, member = member)
            )
            saveFeedRepository.save(SaveFeed(member = member, feed = feed, folder = folder))
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = saveFeedRepository.findByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(10)
        assertThat(page.totalElements).isEqualTo(15)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("회원과 폴더로 저장된 피드를 조회한다")
    fun `회원과 폴더로 저장된 피드를 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder1 = folderRepository.save(Folder(member = member, name = "폴더1", description = null))
        val folder2 = folderRepository.save(Folder(member = member, name = "폴더2", description = null))

        val feed1 = feedRepository.save(Feed(title = "피드1", content = "내용1", board = board, member = member))
        val feed2 = feedRepository.save(Feed(title = "피드2", content = "내용2", board = board, member = member))
        val feed3 = feedRepository.save(Feed(title = "피드3", content = "내용3", board = board, member = member))

        saveFeedRepository.save(SaveFeed(member = member, feed = feed1, folder = folder1))
        saveFeedRepository.save(SaveFeed(member = member, feed = feed2, folder = folder1))
        saveFeedRepository.save(SaveFeed(member = member, feed = feed3, folder = folder2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val folder1Feeds = saveFeedRepository.findByMemberAndFolder(member, folder1, pageable)

        // then
        assertThat(folder1Feeds.content).hasSize(2)
        assertThat(folder1Feeds.content.map { it.feed.title }).containsExactlyInAnyOrder("피드1", "피드2")
    }

    @Test
    @DisplayName("회원과 피드로 저장된 피드를 조회한다")
    fun `회원과 피드로 저장된 피드를 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        val feed = feedRepository.save(
            Feed(title = "테스트 피드", content = "피드 내용", board = board, member = member)
        )

        saveFeedRepository.save(SaveFeed(member = member, feed = feed, folder = folder))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundSaveFeed = saveFeedRepository.findByMemberAndFeed(member, feed)

        // then
        assertThat(foundSaveFeed).isNotNull
        assertThat(foundSaveFeed!!.feed.id).isEqualTo(feed.id)
    }

    @Test
    @DisplayName("회원과 피드로 저장된 피드를 삭제한다")
    fun `회원과 피드로 저장된 피드를 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder = folderRepository.save(Folder(member = member, name = "폴더", description = null))

        val feed = feedRepository.save(
            Feed(title = "테스트 피드", content = "피드 내용", board = board, member = member)
        )

        saveFeedRepository.save(SaveFeed(member = member, feed = feed, folder = folder))
        testEntityManager.flush()

        // when
        saveFeedRepository.deleteByMemberAndFeed(member, feed)
        testEntityManager.flush()

        // then
        val exists = saveFeedRepository.existsByMemberAndFeed(member, feed)
        assertThat(exists).isFalse()
    }

    @Test
    @DisplayName("폴더를 변경한다")
    fun `폴더를 변경한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val folder1 = folderRepository.save(Folder(member = member, name = "폴더1", description = null))
        val folder2 = folderRepository.save(Folder(member = member, name = "폴더2", description = null))

        val feed = feedRepository.save(
            Feed(title = "테스트 피드", content = "피드 내용", board = board, member = member)
        )

        val saveFeed = saveFeedRepository.save(SaveFeed(member = member, feed = feed, folder = folder1))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundSaveFeed = saveFeedRepository.findById(saveFeed.id!!).orElseThrow()
        foundSaveFeed.updateFolder(folder2)
        saveFeedRepository.save(foundSaveFeed)
        testEntityManager.flush()
        testEntityManager.clear()

        // then
        val updatedSaveFeed = saveFeedRepository.findById(saveFeed.id!!).orElseThrow()
        assertThat(updatedSaveFeed.folder.id).isEqualTo(folder2.id)
        assertThat(updatedSaveFeed.folder.name).isEqualTo("폴더2")
    }
}
