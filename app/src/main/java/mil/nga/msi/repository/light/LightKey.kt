package mil.nga.msi.repository.light

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightListItem

@Serializable
@Parcelize
data class LightKey(
   val volumeNumber: String,
   val featureNumber: String,
   val characteristicNumber: Int
): Parcelable {

   fun id(): String {
      return "${volumeNumber}--${featureNumber}--${characteristicNumber}"
   }

   companion object {
      fun fromId(id: String): LightKey {
         val (volumeNumber, featureNumber, characteristicNumber) = id.split("--")
         return LightKey(volumeNumber, featureNumber, characteristicNumber.toInt())
      }

      fun fromLight(light: Light): LightKey {
         return LightKey(light.volumeNumber, light.featureNumber, light.characteristicNumber)
      }

      fun fromLight(item: LightListItem): LightKey {
         return LightKey(item.volumeNumber, item.featureNumber, item.characteristicNumber)
      }
   }
}