package mil.nga.msi.work.radiobeacon

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.radiobeacon.RadioBeaconsTypeAdapter
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadRadioBeaconWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: RadioBeaconLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("radio_beacon.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val beacons = RadioBeaconsTypeAdapter().read(reader)
            dataSource.insert(beacons)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}