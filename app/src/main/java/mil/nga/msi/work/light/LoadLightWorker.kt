package mil.nga.msi.work.light

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.light.LightsTypeAdapter
import mil.nga.msi.repository.light.LightLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadLightWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: LightLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         listOf(
            "light110.json",
            "light111.json",
            "light112.json",
            "light113.json",
            "light114.json",
            "light115.json",
            "light116.json"
         ).forEach { file ->
            context.assets.open(file).use { input ->
               val reader = JsonReader(InputStreamReader(input))
               val lights = LightsTypeAdapter().read(reader).lights
               dataSource.insert(lights)
            }
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}