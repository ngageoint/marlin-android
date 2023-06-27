package mil.nga.msi.repository.bookmark

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.dgpsstation.DgpsStationKey

@Serializable
@Parcelize
data class BookmarkKey(
   val id: String,
   val dataSource: DataSource,
   val notes: String? = null
): Parcelable {
   companion object {
      fun fromAsam(asam: Asam): BookmarkKey {
         return BookmarkKey(asam.reference, DataSource.ASAM, asam.bookmarkNotes)
      }

      fun fromDgpsStation(dgpsStation: DgpsStation): BookmarkKey {
         val id = DgpsStationKey.fromDgpsStation(dgpsStation).id()
         return BookmarkKey(id, DataSource.DGPS_STATION, dgpsStation.bookmarkNotes)
      }

      fun fromModu(modu: Modu): BookmarkKey {
         return BookmarkKey(modu.name, DataSource.MODU, modu.bookmarkNotes)
      }
   }
}