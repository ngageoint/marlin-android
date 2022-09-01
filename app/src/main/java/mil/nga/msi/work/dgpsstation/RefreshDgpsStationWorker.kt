package mil.nga.msi.work.dgpsstation

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository

@HiltWorker
class RefreshDgpsStationWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: DgpsStationRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchDgpsStations(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}