package mil.nga.msi.work.dgpsstation

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.dgpsstations.DgpsStationsTypeAdapter
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadDgpsStationWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: DgpsStationLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("dgps.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val stations = DgpsStationsTypeAdapter().read(reader).dgpsStations
            dataSource.insert(stations)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}