package mil.nga.msi.coordinate

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.truncate

class DMSLocation(
   private val degrees: Int,
   private val minutes: Int,
   private val seconds: Int,
   private val direction: String
) {
   fun toDecimalDegrees(): Double {
      var decimalDegrees =
         degrees.toDouble() +
         (minutes / 60.0) +
         (seconds / 3600.0)

      if (direction == "S" || direction == "W") {
         decimalDegrees = -decimalDegrees
      }

      return decimalDegrees
   }

   fun format(): String {
      val latitudeMinutes = minutes.toString().padStart(2, '0')
      val latitudeSeconds = seconds.toString().padStart(2, '0')
      return "${abs(degrees)}° $latitudeMinutes' $latitudeSeconds\" $direction"
   }
}

class DMS(
   latitude: DMSLocation,
   longitude: DMSLocation
) {
   var latitude: DMSLocation = latitude
      private set

   var longitude: DMSLocation = longitude
      private set


   fun toLatLng(): LatLng {
      return LatLng(latitude.toDecimalDegrees(), longitude.toDecimalDegrees())
   }

   fun format(): String {
      return "${latitude.format()}, ${longitude.format()}"
   }

   companion object {
      fun from(latLng: LatLng): DMS {
         var latDegrees = truncate(latLng.latitude).toInt()
         var latMinutes = abs((latLng.latitude % 1) * 60.0).toInt()
         var latSeconds = (abs((((latLng.latitude % 1) * 60.0) % 1) * 60.0)).roundToInt()
         if (latSeconds == 60) {
            latSeconds = 0
            latMinutes += 1
         }
         if (latMinutes == 60) {
            latDegrees += 1
            latMinutes = 0
         }
         val latitudeDMS = DMSLocation(latDegrees, latMinutes, latSeconds, if (latDegrees >= 0) "N" else "S")

         var lonDegrees = truncate(latLng.longitude).toInt()
         var lonMinutes = abs((latLng.longitude % 1) * 60.0).toInt()
         var lonSeconds = (abs((((latLng.longitude % 1) * 60.0) % 1) * 60.0)).roundToInt()
         if (lonSeconds == 60) {
            lonSeconds = 0
            lonMinutes += 1
         }
         if (lonMinutes == 60) {
            lonDegrees += 1
            lonMinutes = 0
         }
         val longitudeDMS = DMSLocation(lonDegrees, lonMinutes, lonSeconds, if (lonDegrees >= 0) "E" else "W")

         return DMS(latitudeDMS, longitudeDMS)
      }
   }
}
