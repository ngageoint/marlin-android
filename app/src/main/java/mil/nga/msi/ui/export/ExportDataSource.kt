package mil.nga.msi.ui.export

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea

@Parcelize
@Serializable
sealed class ExportDataSource(val dataSource: DataSource) : Parcelable {
   @Parcelize
   @Serializable
   data object Asam: ExportDataSource(DataSource.ASAM)

   @Parcelize
   @Serializable
   data object DgpsStation: ExportDataSource(DataSource.DGPS_STATION)

   @Parcelize
   @Serializable
   data object Light: ExportDataSource(DataSource.LIGHT)

   @Parcelize
   @Serializable
   data object Modu: ExportDataSource(DataSource.MODU)

   @Parcelize
   @Serializable
   data class NavigationalWarning(val navigationArea: NavigationArea? = null): ExportDataSource(DataSource.NAVIGATION_WARNING)

   @Parcelize
   @Serializable
   data object Port: ExportDataSource(DataSource.PORT)

   @Parcelize
   @Serializable
   data object RadioBeacon: ExportDataSource(DataSource.RADIO_BEACON)

   companion object {
      fun fromDataSource(dataSource: DataSource): ExportDataSource? {
         return when (dataSource) {
            DataSource.ASAM -> Asam
            DataSource.DGPS_STATION -> DgpsStation
            DataSource.LIGHT -> Light
            DataSource.MODU -> Modu
            DataSource.NAVIGATION_WARNING -> NavigationalWarning()
            DataSource.PORT -> Port
            DataSource.RADIO_BEACON -> RadioBeacon
            else -> null
         }
      }
   }
}