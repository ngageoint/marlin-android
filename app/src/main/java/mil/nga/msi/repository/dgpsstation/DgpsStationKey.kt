package mil.nga.msi.repository.dgpsstation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.dgpsstation.DgpsStation

@Serializable
@Parcelize
data class DgpsStationKey(
   val volumeNumber: String,
   val featureNumber: Float
): Parcelable {

   fun id(): String {
      return "${volumeNumber}--${featureNumber}"
   }

   companion object {
      fun fromId(id: String): DgpsStationKey {
         val (volumeNumber, featureNumber) = id.split("--")
         return DgpsStationKey(volumeNumber, featureNumber.toFloat())
      }

      fun fromDgpsStation(dgpsStation: DgpsStation): DgpsStationKey {
         return DgpsStationKey(dgpsStation.volumeNumber, dgpsStation.featureNumber)
      }
   }
}