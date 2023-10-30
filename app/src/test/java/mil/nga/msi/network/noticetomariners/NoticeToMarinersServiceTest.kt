package mil.nga.msi.network.noticetomariners

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersLocalDataSource
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Calendar

class NoticeToMarinersServiceTest {

    @Test
    fun testRemoteDataSource() = runTest {
        val noticesNumbers = getNoticeNumbers()

        val mockApplication = mockk<Application>()

        val mockResponse = NoticeToMarinersResponse(
            noticeToMariners = listOf(
                NoticeToMariners(1, "1", 1, "filename"),
                NoticeToMariners(2, "2", 1, "filename")
            )
        )

        val mockService = mockk<NoticeToMarinersService>()
        coEvery {
            mockService.getNoticeToMariners(
                output = "json",
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second
            )
        } returns Response.success(mockResponse)


        val mockDataSource = mockk<NoticeToMarinersLocalDataSource>()
        coEvery {
            mockDataSource.getLatestNoticeToMariners()
        } returns  latestNotice

        val dataSource = NoticeToMarinersRemoteDataSource(mockApplication, mockService, mockDataSource)
        val notices = dataSource.fetchNoticeToMariners()
        assertEquals(2, notices.size)

        coVerify {
            mockService.getNoticeToMariners(
                output = "json",
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second,
            )
        }
    }

    companion object {
        val latestNotice = NoticeToMariners(1, "1", 1, "filename")

        fun getNoticeNumbers(): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)

            val minYear = latestNotice.noticeNumber.toString().take(4)
            val minWeek = latestNotice.noticeNumber.toString().takeLast(2)

            val minNoticeNumber = "${minYear}${"%02d".format(minWeek.toInt() + 1)}"
            val maxNoticeNumber = "${year}${"%02d".format(week + 1)}"

            return Pair(minNoticeNumber, maxNoticeNumber)
        }
    }
}


