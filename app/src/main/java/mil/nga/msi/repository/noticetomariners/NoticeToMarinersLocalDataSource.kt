package mil.nga.msi.repository.noticetomariners

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersDao
import javax.inject.Inject

class NoticeToMarinersLocalDataSource @Inject constructor(
   private val dao: NoticeToMarinersDao
) {
//   fun observeAsams() = dao.observeAsams()
//   fun observeAsam(reference: String) = dao.observeAsam(reference)
//   fun observeAsamMapItems(query: SimpleSQLiteQuery) = dao.observeAsamMapItems(query)
   fun observeNoticeToMarinersListItems() = dao.getNoticeToMarinersListItems()
//
//   fun getAsams(query: SimpleSQLiteQuery) = dao.getAsams(query)
//
   fun isEmpty() = dao.count() == 0

   suspend fun getNoticeToMariners() = dao.getNoticeToMariners()

   suspend fun getNoticeToMariners(noticeNumber: Int) = dao.getNoticeToMariners(noticeNumber)

   suspend fun existingNoticeToMariners(odsEntryIds: List<Int>) = dao.existingNoticeToMariners(odsEntryIds)

   suspend fun insert(noticeToMariners: List<NoticeToMariners>) = dao.insert(noticeToMariners)

   suspend fun getLatestNoticeToMariners() = dao.getLatestNoticeToMariners()

}