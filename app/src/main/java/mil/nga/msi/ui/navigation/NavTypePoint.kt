package mil.nga.msi.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class Point(val latitude: Double, val longitude: Double): Parcelable {
   fun asLatLng() = LatLng(latitude, longitude)

   companion object {
      fun fromLatLng(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)
   }
}

val NavType.Companion.Point: NavType<Point?>
   get() = pointType

   private val pointType = object : NavType<Point?>(true) {
      override fun put(bundle: Bundle, key: String, value: Point?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): Point? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): Point? {
         return Json.decodeFromString(value)
      }
   }