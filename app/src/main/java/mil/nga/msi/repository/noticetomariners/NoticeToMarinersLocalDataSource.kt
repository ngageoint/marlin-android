package mil.nga.msi.repository.noticetomariners

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersDao
import java.nio.file.Files
import javax.inject.Inject

class NoticeToMarinersLocalDataSource @Inject constructor(
   private val application: Application,
   private val dao: NoticeToMarinersDao
) {
   fun observeNoticeToMarinersListItems() = dao.getNoticeToMarinersListItems()
   fun observeNoticeToMariners(noticeNumber: Int) = dao.observeNoticeToMariners(noticeNumber)

   fun isEmpty() = dao.count() == 0

   suspend fun getNoticeToMariners() = dao.getNoticeToMariners()

   suspend fun existingNoticeToMariners(odsEntryIds: List<Int>) = dao.getNoticeToMariners(odsEntryIds)

   suspend fun insert(noticeToMariners: List<NoticeToMariners>) = dao.insert(noticeToMariners)

   suspend fun getLatestNoticeToMariners() = dao.getLatestNoticeToMariners()

   suspend fun deleteNoticeToMarinersPublication(notice: NoticeToMariners) = withContext(Dispatchers.IO) {
      Files.delete(NoticeToMariners.externalFilesPath(application, notice.filename))
   }
}