package mil.nga.msi

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.HiltAndroidApp
import mil.nga.msi.location.LocationFilterService
import mil.nga.msi.location.LocationPolicy
import javax.inject.Inject

@HiltAndroidApp
class MsiApplication: Application(), Configuration.Provider {
   @Inject lateinit var workerFactory: HiltWorkerFactory
   @Inject lateinit var locationPolicy: LocationPolicy
   @Inject lateinit var locationFilterService: LocationFilterService

   override val workManagerConfiguration: Configuration
      get() = Configuration.Builder()
         .setWorkerFactory(workerFactory)
         .build()

   override fun onCreate() {
      super.onCreate()

      requestLocationUpdates()
      startFilterService()
      createNotificationChannel()

      OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
   }

   private fun requestLocationUpdates() {
      if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
         ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         locationPolicy.requestLocationUpdates()
      }
   }

   private fun startFilterService() {
      locationPolicy.filterLocationProvider.observeForever(locationFilterService)
   }

   private fun createNotificationChannel() {
      val channel = MarlinNotificationChannel.create()
      val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
   }
}