package mil.nga.msi.repository.bookmark

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.dgpsstation.DgpsStationKey

@Serializable
@Parcelize
data class BookmarkKey(
   val id: String,
   val dataSource: DataSource,
   val notes: String? = null
): Parcelable {
   companion object {
      fun fromAsam(asam: Asam, notes: String? = null): BookmarkKey {
         return BookmarkKey(asam.reference, DataSource.ASAM, notes)
      }

      fun fromDgpsStation(dgpsStation: DgpsStation, notes: String? = null): BookmarkKey {
         val id = DgpsStationKey.fromDgpsStation(dgpsStation).id()
         return BookmarkKey(id, DataSource.DGPS_STATION, notes)
      }

      fun fromLight(light: Light, notes: String? = null): BookmarkKey {
         return BookmarkKey(light.id, DataSource.LIGHT, notes)
      }

      fun fromModu(modu: Modu, notes: String? = null): BookmarkKey {
         return BookmarkKey(modu.name, DataSource.MODU, notes)
      }

      fun fromNavigationalWarning(warning: NavigationalWarning, notes: String? = null): BookmarkKey {
         return BookmarkKey(warning.id, DataSource.NAVIGATION_WARNING, notes)
      }

      fun fromNavigationalWarning(warning: NavigationalWarningListItem, notes: String? = null): BookmarkKey {
         return BookmarkKey(warning.id, DataSource.NAVIGATION_WARNING, notes)
      }

      fun fromPort(port: Port, notes: String? = null): BookmarkKey {
         return BookmarkKey(port.portNumber.toString(), DataSource.PORT, notes)
      }
   }
}