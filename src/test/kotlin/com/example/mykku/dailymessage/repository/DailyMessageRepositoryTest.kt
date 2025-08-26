package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
class DailyMessageRepositoryTest {

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Test
    fun `findByDateBeforeOrEqualOrderByDateDesc는 지정한 날짜 이전 또는 같은 날짜의 데일리메시지를 내림차순으로 조회한다`() {
        val baseDate = LocalDate.of(2024, 1, 15)
        
        val message1 = DailyMessage(
            title = "1월 10일 메시지",
            content = "과거 메시지",
            date = baseDate.minusDays(5)
        )
        dailyMessageRepository.save(message1)
        
        val message2 = DailyMessage(
            title = "1월 15일 메시지",
            content = "기준일 메시지",
            date = baseDate
        )
        dailyMessageRepository.save(message2)
        
        val message3 = DailyMessage(
            title = "1월 20일 메시지",
            content = "미래 메시지",
            date = baseDate.plusDays(5)
        )
        dailyMessageRepository.save(message3)
        
        val message4 = DailyMessage(
            title = "1월 12일 메시지",
            content = "중간 메시지",
            date = baseDate.minusDays(3)
        )
        dailyMessageRepository.save(message4)

        val pageable = PageRequest.of(0, 10)
        val result = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(baseDate, pageable)

        assertEquals(3, result.size)
        assertEquals("1월 15일 메시지", result[0].title) // 가장 최근 날짜
        assertEquals("1월 12일 메시지", result[1].title) // 중간 날짜
        assertEquals("1월 10일 메시지", result[2].title) // 가장 과거 날짜
        assertTrue(result.none { it.title == "1월 20일 메시지" })
    }

    @Test
    fun `findByDateBeforeOrEqualOrderByDateAsc는 지정한 날짜 이전 또는 같은 날짜의 데일리메시지를 오름차순으로 조회한다`() {
        val baseDate = LocalDate.of(2024, 2, 15)
        
        val message1 = DailyMessage(
            title = "2월 10일 메시지",
            content = "과거 메시지",
            date = baseDate.minusDays(5)
        )
        dailyMessageRepository.save(message1)
        
        val message2 = DailyMessage(
            title = "2월 15일 메시지",
            content = "기준일 메시지",
            date = baseDate
        )
        dailyMessageRepository.save(message2)
        
        val message3 = DailyMessage(
            title = "2월 20일 메시지",
            content = "미래 메시지",
            date = baseDate.plusDays(5)
        )
        dailyMessageRepository.save(message3)
        
        val message4 = DailyMessage(
            title = "2월 12일 메시지",
            content = "중간 메시지",
            date = baseDate.minusDays(3)
        )
        dailyMessageRepository.save(message4)

        val pageable = PageRequest.of(0, 10)
        val result = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateAsc(baseDate, pageable)

        assertEquals(3, result.size)
        assertEquals("2월 10일 메시지", result[0].title) // 가장 과거 날짜
        assertEquals("2월 12일 메시지", result[1].title) // 중간 날짜
        assertEquals("2월 15일 메시지", result[2].title) // 가장 최근 날짜
        assertTrue(result.none { it.title == "2월 20일 메시지" })
    }

    @Test
    fun `findByDateBeforeOrEqualOrderByDateDesc는 조건에 맞는 메시지가 없으면 빈 리스트를 반환한다`() {
        val futureDate = LocalDate.of(2024, 3, 15)
        
        val message = DailyMessage(
            title = "3월 20일 메시지",
            content = "미래 메시지",
            date = futureDate.plusDays(5)
        )
        dailyMessageRepository.save(message)

        val pageable = PageRequest.of(0, 10)
        val result = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(futureDate, pageable)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByDateBeforeOrEqualOrderByDateAsc는 조건에 맞는 메시지가 없으면 빈 리스트를 반환한다`() {
        val futureDate = LocalDate.of(2024, 4, 15)
        
        val message = DailyMessage(
            title = "4월 20일 메시지",
            content = "미래 메시지",
            date = futureDate.plusDays(5)
        )
        dailyMessageRepository.save(message)

        val pageable = PageRequest.of(0, 10)
        val result = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateAsc(futureDate, pageable)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `Pageable을 활용한 페이징이 정상 동작한다`() {
        val baseDate = LocalDate.of(2024, 5, 15)
        
        // 10개의 메시지 생성
        repeat(10) { index ->
            val message = DailyMessage(
                title = "메시지 ${index + 1}",
                content = "내용 ${index + 1}",
                date = baseDate.minusDays(index.toLong())
            )
            dailyMessageRepository.save(message)
        }

        val firstPageable = PageRequest.of(0, 3)
        val firstPage = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(baseDate, firstPageable)

        val secondPageable = PageRequest.of(1, 3)
        val secondPage = dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(baseDate, secondPageable)

        assertEquals(3, firstPage.size)
        assertEquals(3, secondPage.size)
        assertEquals("메시지 1", firstPage[0].title) // 가장 최근 날짜
        assertEquals("메시지 4", secondPage[0].title) // 두 번째 페이지 첫 번째
    }
}