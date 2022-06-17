package mil.nga.msi.work.asam

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
   private val repository: AsamRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchAsams(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}