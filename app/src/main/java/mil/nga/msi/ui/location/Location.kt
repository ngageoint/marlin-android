package mil.nga.msi.ui.location

import android.location.Location
import kotlin.math.roundToInt

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
