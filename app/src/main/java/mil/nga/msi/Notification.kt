package mil.nga.msi

import android.app.*
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.main.MainActivity
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import javax.inject.Inject
import javax.inject.Singleton

private const val MarlinNotificationChannelId = "mil.nga.msi.Marlin"
private const val MarlinNotificationChannelName = "Marlin"
private const val MarlinNotificationChannelImportance = NotificationManager.IMPORTANCE_HIGH

class MarlinNotificationChannel {
   companion object {
      fun create(): NotificationChannel {
         return NotificationChannel(
            MarlinNotificationChannelId,
            MarlinNotificationChannelName,
            MarlinNotificationChannelImportance
         )
      }
   }
}

@Singleton
class MarlinNotification @Inject constructor(
   private val application: Application
) {
   fun asam(asams: List<Asam>) {
      if (asams.isNotEmpty()) {
         val name = if (asams.size == 1) "ASAM" else "ASAMs"
         notification(
            uri = "marlin://${AsamRoute.List.name}".toUri(),
            icon = R.drawable.ic_asam_24dp,
            title = "New $name",
            description = "You have ${asams.size} new $name",
            notificationId = AsamNotificationId
         )
      }
   }

   fun modo(modus: List<Modu>) {
      if (modus.isNotEmpty()) {
         val name = if (modus.size == 1) "MODU" else "MODUs"
         notification(
            uri = "marlin://${ModuRoute.List.name}".toUri(),
            icon = R.drawable.ic_modu_24dp,
            title = "New $name",
            description = "You have ${modus.size} new $name",
            notificationId = ModuNotificationId
         )
      }
   }

   fun navigationWarning(warnings: List<NavigationalWarning>) {
      if (warnings.isNotEmpty()) {
         val name = if (warnings.size == 1) "Navigation Warning" else "Navigation Warnings"
         notification(
            uri = "marlin://${NavigationWarningRoute.Group.name}".toUri(),
            icon = R.drawable.ic_round_warning_24,
            title = "New $name",
            description = "You have ${warnings.size} new $name",
            notificationId = NavigationWarningNotificationId
         )
      }
   }

   fun light(lights: List<Light>) {
      if (lights.isNotEmpty()) {
         val name = if (lights.size == 1) "Light" else "Lights"
         notification(
            uri = "marlin://${LightRoute.List.name}".toUri(),
            icon = R.drawable.ic_baseline_lightbulb_24,
            title = "New $name",
            description = "You have ${lights.size} new $name",
            notificationId = LightNotificationId
         )
      }
   }

   fun port(ports: List<Port>) {
      if (ports.isNotEmpty()) {
         val name = if (ports.size == 1) "World Port" else "World Ports"
         notification(
            uri = "marlin://${PortRoute.List.name}".toUri(),
            icon = R.drawable.ic_baseline_anchor_24,
            title = "New $name",
            description = "You have ${ports.size} new $name",
            notificationId = PortNotificationId
         )
      }
   }

   fun radioBeacon(beacons: List<RadioBeacon>) {
      if (beacons.isNotEmpty()) {
         val name = if (beacons.size == 1) "Radio Beacon" else "Radio Beacons"
         notification(
            uri = "marlin://${RadioBeaconRoute.List.name}".toUri(),
            icon = R.drawable.ic_baseline_settings_input_antenna_24,
            title = "New $name",
            description = "You have ${beacons.size} new $name",
            notificationId = RadioBeaconNotificationId
         )
      }
   }

   fun dgpsStation(stations: List<DgpsStation>) {
      if (stations.isNotEmpty()) {
         val name = if (stations.size == 1) "Differential GPS Station" else "Differential GPS Stations"
         notification(
            uri = "marlin://${DgpsStationRoute.List.name}".toUri(),
            icon = R.drawable.ic_dgps_icon_24,
            title = "New $name",
            description = "You have ${stations.size} new $name",
            notificationId = DgpsStationsNotificationId
         )
      }
   }

   private fun notification(
      uri: Uri,
      icon: Int,
      title: String,
      description: String,
      notificationId: Int
   ) {
      val intent: PendingIntent = TaskStackBuilder.create(application).run {
         addNextIntentWithParentStack(
            Intent(
               Intent.ACTION_VIEW,
               uri,
               application,
               MainActivity::class.java
            )
         )
         getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
      }

      val notification =  NotificationCompat.Builder(application, MarlinNotificationChannelId)
         .setSmallIcon(icon)
         .setContentTitle(title)
         .setContentText(description)
         .setContentIntent(intent)
         .setPriority(NotificationCompat.PRIORITY_MAX)
         .setDefaults(Notification.DEFAULT_ALL)
         .setVibrate(longArrayOf(0))
         .build()

      with(NotificationManagerCompat.from(application)) {
         notify(notificationId, notification)
      }
   }

   companion object {
      private const val AsamNotificationId = 1
      private const val ModuNotificationId = 2
      private const val NavigationWarningNotificationId = 3
      private const val LightNotificationId = 4
      private const val PortNotificationId = 5
      private const val RadioBeaconNotificationId = 6
      private const val DgpsStationsNotificationId = 7
   }
}