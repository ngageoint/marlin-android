package mil.nga.msi.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class Bounds(
   val minLatitude: Double,
   val minLongitude: Double,
   val maxLatitude: Double,
   val maxLongitude: Double
): Parcelable {
   fun asLatLngBounds(): LatLngBounds {
      return LatLngBounds(
         LatLng(minLatitude, minLongitude),
         LatLng(maxLatitude, maxLongitude)
      )
   }

   companion object {
      fun fromLatLngBounds(latLngBounds: LatLngBounds): Bounds {
         return Bounds(
            minLatitude = latLngBounds.southwest.latitude,
            minLongitude = latLngBounds.southwest.longitude,
            maxLatitude = latLngBounds.northeast.latitude,
            maxLongitude = latLngBounds.northeast.longitude
         )
      }
   }
}

val NavType.Companion.NavTypeBounds: NavType<Bounds?>
   get() = boundsType

   private val boundsType = object : NavType<Bounds?>(true) {
      override fun put(bundle: Bundle, key: String, value: Bounds?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): Bounds? {
         return BundleCompat.getParcelable(bundle, key, Bounds::class.java)
      }

      override fun parseValue(value: String): Bounds? {
         return Json.decodeFromString(value)
      }
   }