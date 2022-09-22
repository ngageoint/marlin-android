package mil.nga.msi.work.modu

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.modu.ModusTypeAdapter
import mil.nga.msi.repository.modu.ModuLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadModuWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: ModuLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("modu.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val modus = ModusTypeAdapter().read(reader)
            dataSource.insert(modus)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}