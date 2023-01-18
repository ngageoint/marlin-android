package mil.nga.msi.repository.noticetomariners

import android.util.Log
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.network.noticetomariners.NoticeToMarinersService
import java.util.*
import javax.inject.Inject

class NoticeToMarinersRemoteDataSource @Inject constructor(
   private val service: NoticeToMarinersService,
   private val localDataSource: NoticeToMarinersLocalDataSource
) {
   suspend fun fetchNoticeToMariners(): List<NoticeToMariners> {
      val noticeToMariners = mutableListOf<NoticeToMariners>()

      val latestNoticeToMariners = localDataSource.getLatestNoticeToMariners()
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestNoticeToMariners != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         val minYear = latestNoticeToMariners.noticeNumber.toString().take(4)
         val minWeek = latestNoticeToMariners.noticeNumber.toString().takeLast(2)

         minNoticeNumber = "${minYear}${"%02d".format(minWeek.toInt() + 1)}"
         maxNoticeNumber = "${year}${"%02d".format(week + 1)}"
      }

      val response = service.getNoticeToMariners(
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (response.isSuccessful) {
         val body = response.body()
         body?.let { noticeToMariners.addAll(it) }
      }

      return noticeToMariners
   }
}