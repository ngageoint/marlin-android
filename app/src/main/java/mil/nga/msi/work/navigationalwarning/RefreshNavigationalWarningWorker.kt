package mil.nga.msi.work.navigationalwarning

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository

@HiltWorker
class RefreshNavigationalWarningWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted params: WorkerParameters,
   private val repository: NavigationalWarningRepository,
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      repository.fetchNavigationalWarnings(true)
      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}