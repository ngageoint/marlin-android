package mil.nga.msi.ui.location

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.sf.Point
import kotlin.math.*

fun Location.generalDirection(location: Location): String {
   val directions = listOf("N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW", "NW", "NNW")
   val bearingCorrection = 360.0 / directions.size * 2.0
   val indexDegrees = 360.0 / directions.size

   var bearing = bearingTo(location).toDouble()
   bearing += bearingCorrection
   if (bearing < 0) {
      bearing += 360
   }
   if (bearing > 360) {
      bearing -= 360
   }
   val index = (bearing / indexDegrees).roundToInt() % directions.size
   return directions[index]
}

fun Point.wgs84ToWebMercator(): Point {
   val a = 6378137.0
   val lambda = x / 180 * PI
   val phi = y / 180 * PI
   val x = a * lambda
   val y = a * ln(tan(PI / 4 + phi / 2))

   return Point(x, y)
}

fun Point.webMercatorToWgs84(): Point {
   val a = 6378137.0
   val d = -y / a
   val phi = PI / 2 - 2 * atan(exp(d))
   val lambda = x / a
   val latitude = phi / PI * 180
   val longitude = lambda / PI * 180

   return Point(longitude, latitude)
}

fun LatLng.toPixel(tileBounds3857: LatLngBounds, tileSize: Double): Point {
   val object3857Location = to3857()
   val xPosition = (((object3857Location.x - tileBounds3857.southwest.longitude) / (tileBounds3857.northeast.longitude - tileBounds3857.southwest.longitude)) * tileSize)
   val yPosition = tileSize - (((object3857Location.y - tileBounds3857.southwest.latitude) / (tileBounds3857.northeast.latitude - tileBounds3857.southwest.latitude)) * tileSize)
   return Point(xPosition, yPosition)
}

fun LatLng.to3857(): Point {
   val a = 6378137.0
   val lambda = longitude / 180 * PI
   val phi = latitude / 180 *  PI
   val x = a * lambda
   val y = a * ln(tan(PI / 4 + phi / 2))

   return Point(x, y)
}

fun Double.toRadians(): Double {
   return this * PI / 180.0
}

fun Double.toDegrees(): Double {
   return this * 180.0 / PI
}
