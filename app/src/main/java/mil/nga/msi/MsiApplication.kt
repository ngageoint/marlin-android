package mil.nga.msi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MsiApplication: Application(), Configuration.Provider {
   @Inject
   lateinit var workerFactory: HiltWorkerFactory

   override fun getWorkManagerConfiguration() =
      Configuration.Builder()
         .setWorkerFactory(workerFactory)
         .build()
}