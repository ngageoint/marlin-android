package mil.nga.msi.repository.dgpsstation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationListItem

@Serializable
@Parcelize
data class DgpsStationKey(
   val volumeNumber: String,
   val featureNumber: Int
): Parcelable {

   fun id(): String {
      return "${volumeNumber}--${featureNumber}"
   }

   companion object {
      fun fromId(id: String): DgpsStationKey {
         val (volumeNumber, featureNumber) = id.split("--")
         return DgpsStationKey(volumeNumber, featureNumber.toInt())
      }

      fun fromDgpsStation(dgpsStation: DgpsStation): DgpsStationKey {
         return DgpsStationKey(dgpsStation.volumeNumber, dgpsStation.featureNumber)
      }

      fun fromDgpsStation(item: DgpsStationListItem): DgpsStationKey {
         return DgpsStationKey(item.volumeNumber, item.featureNumber)
      }
   }
}