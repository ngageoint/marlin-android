package mil.nga.msi.work.noticetomariners

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class RefreshNoticeToMarinersWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: NoticeToMarinersRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : CoroutineWorker(context, params) {

   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.NOTICE_TO_MARINERS)
      if (fetched == null || fetched.isBefore(Instant.now().minus(FETCH_INTERVAL_HOURS, ChronoUnit.HOURS))) {
         repository.fetchNoticeToMariners(true)
         userPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, Instant.now())
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   override suspend fun getForegroundInfo(): ForegroundInfo {
      return ForegroundInfo(notification.notificationIdForFetching(DataSource.NOTICE_TO_MARINERS), notification.notificationForFetching(DataSource.NOTICE_TO_MARINERS))
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}