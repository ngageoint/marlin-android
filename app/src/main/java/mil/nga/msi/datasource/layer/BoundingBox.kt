package mil.nga.msi.datasource.layer

import android.os.Parcelable
import androidx.room.Ignore
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Parcelize
@Serializable
data class BoundingBox(
   val minLatitude: Double,
   val minLongitude: Double,
   val maxLatitude: Double,
   val maxLongitude: Double
): Parcelable {
   @Ignore
   @Transient
   @IgnoredOnParcel
   val latLngBounds = LatLngBounds(
      LatLng(minLatitude, minLongitude),
      LatLng(maxLatitude, maxLongitude)
   )

   companion object {
      fun fromLatLngBounds(latLngBounds: LatLngBounds): BoundingBox {
         return BoundingBox(
            minLatitude = latLngBounds.southwest.latitude,
            minLongitude = latLngBounds.southwest.longitude,
            maxLatitude = latLngBounds.northeast.latitude,
            maxLongitude = latLngBounds.northeast.longitude
         )
      }
   }
}