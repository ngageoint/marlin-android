package mil.nga.msi.startup.asam

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import mil.nga.msi.di.AppInitializer
import javax.inject.Inject

class WorkManagerInitializer : Initializer<WorkManager> {
   @Inject
   lateinit var workerFactory: HiltWorkerFactory

   override fun create(context: Context): WorkManager {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      val configuration = Configuration.Builder()
         .setWorkerFactory(workerFactory)
         .build()

      WorkManager.initialize(context, configuration)

      return WorkManager.getInstance(context)
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return emptyList()
   }
}