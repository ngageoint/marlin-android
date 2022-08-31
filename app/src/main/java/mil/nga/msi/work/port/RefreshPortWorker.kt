package mil.nga.msi.work.port

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.port.PortRepository

@HiltWorker
class RefreshPortWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: PortRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchPorts(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}