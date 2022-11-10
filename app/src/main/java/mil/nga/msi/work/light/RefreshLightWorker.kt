package mil.nga.msi.work.light

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class RefreshLightWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: LightRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.LIGHT)
      if (fetched == null || fetched.isBefore(Instant.now().minus(FETCH_INTERVAL_HOURS, ChronoUnit.HOURS))) {
         repository.fetchLights(true)
         userPreferencesRepository.setFetched(DataSource.LIGHT, Instant.now())
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   override suspend fun getForegroundInfo(): ForegroundInfo {
      return ForegroundInfo(notification.notificationIdForFetching(DataSource.LIGHT), notification.notificationForFetching(DataSource.LIGHT))
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}