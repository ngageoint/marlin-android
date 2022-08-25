package mil.nga.msi.work.radiobeacon

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository

@HiltWorker
class RefreshRadioBeaconWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: RadioBeaconRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchRadioBeacons(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}