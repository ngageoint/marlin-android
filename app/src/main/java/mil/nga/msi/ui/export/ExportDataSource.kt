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
   object Asam: ExportDataSource(DataSource.ASAM)

   @Parcelize
   @Serializable
   object DgpsStation: ExportDataSource(DataSource.DGPS_STATION)

   @Parcelize
   @Serializable
   object Light: ExportDataSource(DataSource.LIGHT)

   @Parcelize
   @Serializable
   object Modu: ExportDataSource(DataSource.MODU)

   @Parcelize
   @Serializable
   data class NavigationalWarning(val navigationArea: NavigationArea? = null): ExportDataSource(DataSource.NAVIGATION_WARNING)

   @Parcelize
   @Serializable
   object Port: ExportDataSource(DataSource.PORT)

   @Parcelize
   @Serializable
   object RadioBeacon: ExportDataSource(DataSource.RADIO_BEACON)
}