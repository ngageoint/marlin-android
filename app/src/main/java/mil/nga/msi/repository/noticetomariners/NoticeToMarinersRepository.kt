package mil.nga.msi.repository.noticetomariners

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.startup.asam.AsamInitializer.Companion.FETCH_LATEST_ASAMS_TASK
import javax.inject.Inject

class NoticeToMarinersRepository @Inject constructor(
   workManager: WorkManager,
   private val localDataSource: NoticeToMarinersLocalDataSource,
   private val remoteDataSource: NoticeToMarinersRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {
//   val asams = localDataSource.observeAsams()
//
//   fun observeAsam(reference: String) = localDataSource.observeAsam(reference)
//
//   @OptIn(ExperimentalCoroutinesApi::class)
//   fun observeAsamMapItems(): Flow<List<AsamMapItem>> {
//      return filterRepository.filters.flatMapLatest { entry ->
//         val filters = entry[DataSource.ASAM] ?: emptyList()
//         val query = QueryBuilder("asams", filters).buildQuery()
//         localDataSource.observeAsamMapItems(query)
//      }
//   }

   suspend fun getNoticeToMariners(noticeNumber: Int) = localDataSource.getNoticeToMariners(noticeNumber)

   suspend fun observeNoticeToMarinersGraphics(noticeNumber: Int): List<NoticeToMarinersGraphics> {
      return remoteDataSource.fetchNoticeToMarinersGraphics(noticeNumber)
   }

   fun observeNoticeToMarinersListItems(): Flow<List<NoticeToMariners>> {
      return localDataSource.observeNoticeToMarinersListItems()
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

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_ASAMS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}