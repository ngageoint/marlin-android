package mil.nga.msi.work.port

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.port.PortsTypeAdapter
import mil.nga.msi.repository.port.PortLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadPortWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: PortLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("port.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val ports = PortsTypeAdapter().read(reader)
            dataSource.insert(ports)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}