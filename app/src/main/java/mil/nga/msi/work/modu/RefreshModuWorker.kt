package mil.nga.msi.work.modu

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class RefreshModuWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: ModuRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.MODU)
      if (fetched == null || fetched.isBefore(Instant.now().minus(FETCH_INTERVAL_HOURS, ChronoUnit.HOURS))) {
         repository.fetchModus(true)
         userPreferencesRepository.setFetched(DataSource.MODU, Instant.now())
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}