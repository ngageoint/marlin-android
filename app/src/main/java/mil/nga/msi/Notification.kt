package mil.nga.msi

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.main.MainActivity
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import javax.inject.Inject
import javax.inject.Singleton


class MarlinNotificationChannel {
   companion object {

      const val Id = "mil.nga.msi.Marlin"
      const val Name = "Marlin"
      private const val Importance = NotificationManager.IMPORTANCE_HIGH

      fun create(): NotificationChannel {
         return NotificationChannel(Id, Name, Importance)
      }
   }
}

@Singleton
class MarlinNotification @Inject constructor(
   private val application: Application
) {

   fun asam(asams: List<Asam>) {
      if (asams.isNotEmpty()) {
         val name = DataSource.ASAM.labelForCount(asams.size)
         notify(
            notificationId = AsamNotificationId,
            title = "New $name",
            description = "You have ${asams.size} new $name",
            icon = DataSource.ASAM.icon,
            uri = "marlin://${AsamRoute.List.name}".toUri()
         )
      }
   }

   fun modu(modus: List<Modu>) {
      if (modus.isNotEmpty()) {
         val name = DataSource.MODU.labelForCount(modus.size)
         notify(
            notificationId = ModuNotificationId,
            title = "New $name",
            description = "You have ${modus.size} new $name",
            icon = DataSource.MODU.icon,
            uri = "marlin://${ModuRoute.List.name}".toUri()
         )
      }
   }

   fun navigationWarning(warnings: List<NavigationalWarning>) {
      if (warnings.isNotEmpty()) {
         val name = DataSource.NAVIGATION_WARNING.labelForCount(warnings.size)
         notify(
            notificationId = NavigationWarningNotificationId,
            title = "New $name",
            description = "You have ${warnings.size} new $name",
            icon = DataSource.NAVIGATION_WARNING.icon,
            uri = "marlin://${NavigationWarningRoute.Group.name}".toUri()
         )
      }
   }

   fun light(lights: List<Light>) {
      if (lights.isNotEmpty()) {
         val name = DataSource.LIGHT.labelForCount(lights.size)
         notify(
            notificationId = LightNotificationId,
            title = "New $name",
            description = "You have ${lights.size} new $name",
            icon = DataSource.LIGHT.icon,
            uri = "marlin://${LightRoute.List.name}".toUri()
         )
      }
   }

   fun port(ports: List<Port>) {
      if (ports.isNotEmpty()) {
         val name = DataSource.PORT.labelForCount(ports.size)
         notify(
            notificationId = PortNotificationId,
            title = "New $name",
            description = "You have ${ports.size} new $name",
            icon = DataSource.PORT.icon,
            uri = "marlin://${PortRoute.List.name}".toUri()
         )
      }
   }

   fun radioBeacon(beacons: List<RadioBeacon>) {
      if (beacons.isNotEmpty()) {
         val name = DataSource.RADIO_BEACON.labelForCount(beacons.size)
         notify(
            notificationId = RadioBeaconNotificationId,
            title = "New $name",
            description = "You have ${beacons.size} new $name",
            icon = DataSource.RADIO_BEACON.icon,
            uri = "marlin://${RadioBeaconRoute.List.name}".toUri()
         )
      }
   }

   fun dgpsStation(stations: List<DgpsStation>) {
      if (stations.isNotEmpty()) {
         val name = DataSource.DGPS_STATION.labelForCount(stations.size)
         notify(
            notificationId = DgpsStationsNotificationId,
            title = "New $name",
            description = "You have ${stations.size} new $name",
            icon = DataSource.DGPS_STATION.icon,
            uri = "marlin://${DgpsStationRoute.List.name}".toUri()
         )
      }
   }

   fun noticeToMariners(notices: List<NoticeToMariners>) {
      if (notices.isNotEmpty()) {
         val name = DataSource.NOTICE_TO_MARINERS.labelForCount(notices.size)
         notify(
            notificationId = NoticeToMarinersNotificationId,
            title = "New $name",
            description = "You have ${notices.size} new $name",
            icon = DataSource.NOTICE_TO_MARINERS.icon,
            uri = "marlin://${NoticeToMarinersRoute.All.name}".toUri()
         )
      }
   }

   fun notificationForFetching(source: DataSource): Notification {
      return NotificationCompat.Builder(application, MarlinNotificationChannel.Id)
         .setContentTitle("Checking for new ${source.labelPlural}")
         .setSmallIcon(source.icon)
         .setPriority(NotificationCompat.PRIORITY_DEFAULT)
         .setAutoCancel(true)
         .build()
   }

   fun notificationIdForFetching(source: DataSource): Int {
      return when (source) {
         DataSource.ASAM -> AsamFetchNotificationId
         DataSource.MODU -> ModuFetchNotificationId
         DataSource.NAVIGATION_WARNING -> NavigationWarningFetchNotificationId
         DataSource.LIGHT -> LightFetchNotificationId
         DataSource.PORT -> PortFetchNotificationId
         DataSource.RADIO_BEACON -> RadioBeaconFetchNotificationId
         DataSource.DGPS_STATION -> DgpsStationsFetchNotificationId
         DataSource.ELECTRONIC_PUBLICATION -> ElectronicPublicationFetchNotificationId
         DataSource.NOTICE_TO_MARINERS -> NoticeToMarinersFetchNotificationId
         DataSource.GEOPACKAGE -> -1
      }
   }

   private fun notify(
      notificationId: Int,
      title: String,
      description: String,
      icon: Int,
      uri: Uri
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

      val notification =  NotificationCompat.Builder(application, MarlinNotificationChannel.Id)
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
      private const val AsamFetchNotificationId = 10
      private const val ModuNotificationId = 2
      private const val ModuFetchNotificationId = 20
      private const val NavigationWarningNotificationId = 3
      private const val NavigationWarningFetchNotificationId = 30
      private const val LightNotificationId = 4
      private const val LightFetchNotificationId = 40
      private const val PortNotificationId = 5
      private const val PortFetchNotificationId = 50
      private const val RadioBeaconNotificationId = 6
      private const val RadioBeaconFetchNotificationId = 60
      private const val DgpsStationsNotificationId = 7
      private const val DgpsStationsFetchNotificationId = 70
      private const val ElectronicPublicationNotificationId = 8
      private const val ElectronicPublicationFetchNotificationId = 80
      private const val NoticeToMarinersNotificationId = 9
      private const val NoticeToMarinersFetchNotificationId = 90
   }
}