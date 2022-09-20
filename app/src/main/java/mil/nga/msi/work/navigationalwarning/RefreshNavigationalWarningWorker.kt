package mil.nga.msi.work.navigationalwarning

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
   private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)
      if (fetched == null || fetched.isBefore(Instant.now().minus(FETCH_INTERVAL_HOURS, ChronoUnit.HOURS))) {
         repository.fetchNavigationalWarnings(true)
         userPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, Instant.now())
      }
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}