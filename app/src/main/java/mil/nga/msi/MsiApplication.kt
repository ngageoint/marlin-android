package mil.nga.msi

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

   override fun onCreate() {
      super.onCreate()
      createNotificationChannel()
   }

   private fun createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val channel = MarlinNotificationChannel.create()
         val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         notificationManager.createNotificationChannel(channel)
      }
   }
}