package mil.nga.msi.work.asam

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.asam.AsamsTypeAdapter
import mil.nga.msi.repository.asam.AsamLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadAsamWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: AsamLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("asam.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val asams = AsamsTypeAdapter().read(reader)
            dataSource.insert(asams)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}