package mil.nga.msi.startup

import android.content.Context
import android.util.Log
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
      val initializer = AppInitializer.resolve(context)
      initializer.inject(this)

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