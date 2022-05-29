package mil.nga.msi.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.asam.AsamRepository

@HiltWorker
class RefreshAsamWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val asamRepository: AsamRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      asamRepository.getAsams(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}