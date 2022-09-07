package mil.nga.msi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val MarlinNotificationChannelId = "mil.nga.msi.Marlin"
private const val MarlinNotificationChannelName = "Marlin"
private const val MarlinNotificationChannelImportance = NotificationManager.IMPORTANCE_HIGH

class MarlinNotificationChannel {
   companion object {
      @RequiresApi(Build.VERSION_CODES.O)
      fun create(): NotificationChannel {
         return NotificationChannel(
            MarlinNotificationChannelId,
            MarlinNotificationChannelName,
            MarlinNotificationChannelImportance
         )
      }
   }
}

class MarlinNotification {
   companion object {
      private const val AsamNotificationId = 1

      // TODO no notification if original tile load
      fun asam(context: Context, count: Int) {
         if (count > 0 ) {
            val notification =  NotificationCompat.Builder(context, MarlinNotificationChannelId)
               .setSmallIcon(R.drawable.asam_map_marker_24dp)
               .setContentTitle("New ASAMs")
               .setContentText("You have $count new ${if (count == 1) "ASAM" else "ASAMs"}")
               .setPriority(NotificationCompat.PRIORITY_MAX)
               .setDefaults(Notification.DEFAULT_ALL)
               .setVibrate(longArrayOf(0))
               .build()

            with(NotificationManagerCompat.from(context)) {
               notify(AsamNotificationId, notification)
            }
         }
      }
   }
}