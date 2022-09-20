package mil.nga.msi.work.radiobeacon

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class RefreshRadioBeaconWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: RadioBeaconRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      val fetched = userPreferencesRepository.fetched(DataSource.RADIO_BEACON)
      if (fetched == null || fetched.isBefore(Instant.now().minus(FETCH_INTERVAL_HOURS, ChronoUnit.HOURS))) {
         repository.fetchRadioBeacons(true)
         userPreferencesRepository.setFetched(DataSource.RADIO_BEACON, Instant.now())
      }
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }

   companion object {
      private const val FETCH_INTERVAL_HOURS = 24L
   }
}