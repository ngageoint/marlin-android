package mil.nga.msi.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.type.MapLocation

@Serializable
@Parcelize
data class NavPoint(val latitude: Double, val longitude: Double): Parcelable {
   fun asMapLocation(zoom: Float): MapLocation {
      return MapLocation.newBuilder()
         .setLatitude(latitude)
         .setLongitude(longitude)
         .setZoom(zoom.toDouble())
         .build()
   }
}

val NavType.Companion.NavTypePoint: NavType<NavPoint?>
   get() = pointType

   private val pointType = object : NavType<NavPoint?>(true) {
      override fun put(bundle: Bundle, key: String, value: NavPoint?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): NavPoint? {
         return BundleCompat.getParcelable(bundle, key, NavPoint::class.java)
      }

      override fun parseValue(value: String): NavPoint? {
         return Json.decodeFromString(value)
      }
   }