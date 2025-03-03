package mil.nga.msi.work.navigationalwarning

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class RefreshNavigationalWarningWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: NavigationalWarningRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)
      if (true) {
         repository.fetchNavigationalWarnings(true)
         userPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, Instant.now())
      }
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   override suspend fun getForegroundInfo(): ForegroundInfo {
      return ForegroundInfo(notification.notificationIdForFetching(DataSource.NAVIGATION_WARNING), notification.notificationForFetching(DataSource.NAVIGATION_WARNING))
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}