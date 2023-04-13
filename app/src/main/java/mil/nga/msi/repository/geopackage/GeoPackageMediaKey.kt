package mil.nga.msi.repository.geopackage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GeoPackageMediaKey(
   val layerId: Long,
   val table: String,
   val mediaId: Long
): Parcelable {

   fun id(): String {
      return "${layerId}--${table}--${mediaId}"
   }
}