package mil.nga.msi.repository.geopackage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GeoPackageFeatureKey(
   val layerId: Long,
   val table: String,
   val featureId: Long
): Parcelable {

   fun id(): String {
      return "${layerId}--${table}--${featureId}"
   }

   companion object {
      fun fromId(id: String): GeoPackageFeatureKey {
         val (layerId, table, featureId) = id.split("--")
         return GeoPackageFeatureKey(layerId.toLong(), table, featureId.toLong())
      }
   }
}