package mil.nga.msi.work.light

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.light.LightRepository

@HiltWorker
class RefreshLightWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: LightRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchLights(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}