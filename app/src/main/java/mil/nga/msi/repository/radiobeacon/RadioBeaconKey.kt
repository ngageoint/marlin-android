package mil.nga.msi.repository.radiobeacon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.radiobeacon.RadioBeacon

@Serializable
@Parcelize
data class RadioBeaconKey(
   val volumeNumber: String,
   val featureNumber: String
): Parcelable {

   fun id(): String {
      return "${volumeNumber}--${featureNumber}"
   }

   companion object {
      fun fromId(id: String): RadioBeaconKey {
         val (volumeNumber, featureNumber) = id.split("--")
         return RadioBeaconKey(volumeNumber, featureNumber)
      }

      fun fromRadioBeacon(beacon: RadioBeacon): RadioBeaconKey {
         return RadioBeaconKey(beacon.volumeNumber, beacon.featureNumber)
      }
   }
}