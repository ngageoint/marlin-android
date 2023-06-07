package mil.nga.msi.coordinate

import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

private val latLngFormat = DecimalFormat("###.00000")

enum class CoordinateSystem(val title: String) {
   WGS84("Latitude, Longitude"),
   DMS("Degrees, Minutes, Seconds"),
   MGRS("Military Grid Reference System"),
   GARS("Global Area Reference System");

   fun format(latLng: LatLng): String {
      return when (this) {
         WGS84 -> {
           latLngFormat.format(latLng.latitude) + ", " + latLngFormat.format(latLng.longitude)
         }
         MGRS -> {
            val mgrs = mil.nga.mgrs.MGRS.from(mil.nga.grid.features.Point.point(latLng.longitude, latLng.latitude))
            mgrs.coordinate()
         }
         DMS -> {
            mil.nga.msi.coordinate.DMS.from(latLng).format()
         }
         GARS -> {
            val gars = mil.nga.gars.GARS.from(mil.nga.grid.features.Point.point(latLng.longitude, latLng.latitude))
            gars.coordinate()
         }
      }
   }

   companion object {
      fun fromName(name: String?): CoordinateSystem {
         val coordinateSystem = name?.let {
            try {
               CoordinateSystem.valueOf(it)
            } catch(_: Exception) { null }
         }

         return coordinateSystem ?: DMS
      }
   }
}