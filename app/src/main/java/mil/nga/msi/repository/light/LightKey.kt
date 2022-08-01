package mil.nga.msi.repository.light

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.light.Light

@Serializable
@Parcelize
data class LightKey(
   val volumeNumber: String,
   val featureNumber: String,
   val characteristicNumber: Int
): Parcelable {
   companion object {
      fun fromLight(light: Light): LightKey {
         return LightKey(light.volumeNumber, light.featureNumber, light.characteristicNumber)
      }
   }
}