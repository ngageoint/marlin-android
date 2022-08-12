package mil.nga.msi.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.type.MapLocation

@Serializable
@Parcelize
data class Point(val latitude: Double, val longitude: Double): Parcelable {
   fun asMapLocation(zoom: Float): MapLocation {
      return MapLocation.newBuilder()
         .setLatitude(latitude)
         .setLongitude(longitude)
         .setZoom(zoom.toDouble())
         .build()
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