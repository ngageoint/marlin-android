package mil.nga.msi.repository.noticetomariners

import android.net.Uri
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.startup.asam.AsamInitializer.Companion.FETCH_LATEST_ASAMS_TASK
import javax.inject.Inject

class NoticeToMarinersRepository @Inject constructor(
   workManager: WorkManager,
   private val localDataSource: NoticeToMarinersLocalDataSource,
   private val remoteDataSource: NoticeToMarinersRemoteDataSource,
   private val notification: MarlinNotification,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   fun observeNoticeToMariners(noticeNumber: Int) = localDataSource.observeNoticeToMariners(noticeNumber)

   fun observeNoticeToMarinersListItems(): Flow<List<NoticeToMariners>> {
      return localDataSource.observeNoticeToMarinersListItems()
   }

   suspend fun getNoticeToMarinersGraphics(noticeNumber: Int): List<NoticeToMarinersGraphics> {
      return remoteDataSource.fetchNoticeToMarinersGraphics(noticeNumber)
   }

   suspend fun fetchNoticeToMariners(refresh: Boolean = false): List<NoticeToMariners> {
      if (refresh) {
         val noticeToMariners = remoteDataSource.fetchNoticeToMariners()

         val fetched = userPreferencesRepository.fetched(DataSource.NOTICE_TO_MARINERS)
         if (fetched != null) {
            val newNoticeToMariners = noticeToMariners.subtract(localDataSource.existingNoticeToMariners(noticeToMariners.map { it.odsEntryId }).toSet()).toList()
            notification.noticeToMariners(newNoticeToMariners)
         }

         localDataSource.insert(noticeToMariners)
      }

      return localDataSource.getNoticeToMariners()
   }

   suspend fun getNoticeToMarinersPublication(notice: NoticeToMariners): Uri? {
      return remoteDataSource.fetchNoticeToMarinersPublication(notice)
   }

   suspend fun deleteNoticeToMarinersPublication(notice: NoticeToMariners) {
      localDataSource.deleteNoticeToMarinersPublication(notice)
   }

   suspend fun getNoticeToMarinersGraphic(graphic: NoticeToMarinersGraphic): Uri? {
      return remoteDataSource.fetchNoticeToMarinersGraphic(graphic)
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_ASAMS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   suspend fun getNoticeToMarinersCorrections(
      locationFilter: Filter?,
      noticeFilter: Filter?
   ): List<ChartCorrection> {
      return remoteDataSource.getNoticeToMarinersCorrections(locationFilter, noticeFilter)
   }
}