package mil.nga.msi.coordinate

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.truncate

enum class CoordinateType {
   LATITUDE, LONGITUDE
}

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

   companion object {
      fun parse(location: String, type: CoordinateType, addDirection: Boolean = false): DMSLocation? {
         val charactersToKeep = if (type == CoordinateType.LATITUDE) "-.NS" else "-.EW"
         var parsable = location.filter {
            it.isDigit() || charactersToKeep.contains(it.uppercase())
         }

         if (parsable.isEmpty()) return null

         val direction = if (addDirection) {
            when {
               parsable.first() == '-' -> {
                  if (type == CoordinateType.LATITUDE) "S" else "W"
               }
               else -> if (type == CoordinateType.LATITUDE) "N" else "E"
            }
         } else {
            when {
               parsable.last().isLetter() -> {
                  val direction = parsable.last().uppercase()
                  parsable = parsable.dropLast(1)
                  direction
               }
               parsable.first().isLetter() -> {
                  val direction = parsable.first().uppercase()
                  parsable = parsable.drop(1)
                  direction
               }
               else -> null
            }
         }

         parsable = parsable.filter { it.isDigit() || it == '.' }

         val split = parsable.split(".")
         parsable = split.first()
         val decimalSeconds = if (split.size == 2) split[1].toIntOrNull() else null

         var seconds: Int? = parsable.takeLast(2).toIntOrNull()
         parsable = parsable.dropLast(2)

         var minutes = if (parsable.isNotEmpty()) parsable.takeLast(2).toIntOrNull() else null
         var degrees = if (parsable.isNotEmpty()) parsable.dropLast(2).toIntOrNull() else null

         if (degrees == null) {
            if (minutes == null) {
               degrees = seconds
               seconds = null
            } else {
               degrees = minutes
               minutes = seconds
               seconds = null
            }
         }

         if (minutes == null && seconds == null && decimalSeconds != null) {
            // this would be the case if a decimal degrees was passed in ie 11.123
            val decimal = ".${decimalSeconds}".toDoubleOrNull() ?: 0.0
            minutes = abs((decimal % 1) * 60.0).toInt()
            seconds = abs((((decimal % 1) * 60.0) % 1) * 60.0).roundToInt()
         } else if (decimalSeconds != null) {
            // add the decimal seconds to seconds and round
            seconds = "${seconds ?: 0}.${decimalSeconds}".toDouble().roundToInt()
         }

         val validDegrees = degrees?.let {
            if (type == CoordinateType.LATITUDE) it in 0..90 else it in 0..180
         } ?: false

         val validMinutes = minutes?.let {
            !((it < 0 || it > 59) || (type == CoordinateType.LATITUDE && degrees == 90 && minutes != 0) || (type == CoordinateType.LONGITUDE && degrees == 180 && minutes != 0))
         } ?: false

         val validSeconds = seconds?.let {
            !((it < 0 || it > 59) || (type == CoordinateType.LATITUDE && degrees == 90 && seconds != 0) || (type == CoordinateType.LONGITUDE && degrees == 180 && seconds != 0))
         } ?: false

         if (!validDegrees || !validMinutes || !validSeconds) return null

         return if (degrees != null && minutes != null && seconds != null && direction != null) {
            DMSLocation(degrees, minutes, seconds, direction)
         } else null
      }
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

      fun from(location: String): DMS? {
         val coordinates = splitCoordinates(location)
         if (coordinates.size != 2) return null

         val dmsLatitude = DMSLocation.parse(coordinates.first(), CoordinateType.LATITUDE)
         val dmsLongitude = DMSLocation.parse(coordinates.last(), CoordinateType.LONGITUDE)

         return if (dmsLatitude != null && dmsLongitude != null) {
            DMS(dmsLatitude, dmsLongitude)
         } else null
      }

      private fun splitCoordinates(text: String): List<String> {
         val coordinates = mutableListOf<String>()

         val parsable = text.lines().joinToString().trim()

         // if there is a comma, split on that
         if (parsable.contains(',')) {
            return parsable.split(",").map { coordinate ->
               coordinate.trim()
            }
         }

         // Check if there are any direction letters
         val firstDirectionIndex = parsable.indexOfFirst { "NSEW".contains(it.uppercase()) }
         if (firstDirectionIndex != -1) {
            if (parsable.contains('-')) {
               // Split coordinates on dash
               return parsable.split("-").map { coordinate ->
                  coordinate.trim()
               }
            } else {
               // No dash, split on the direction
               val lastDirectionIndex = parsable.indexOfLast { "NSEW".contains(it.uppercase()) }

               if (firstDirectionIndex == 0) {
                  if (lastDirectionIndex != 0) {
                     return listOf(
                        parsable.substring(0, lastDirectionIndex - 1),
                        parsable.substring(lastDirectionIndex)
                     )
                  }
               } else if (lastDirectionIndex == parsable.lastIndex) {
                  if (lastDirectionIndex != firstDirectionIndex) {
                     return listOf(
                        parsable.substring(0, firstDirectionIndex + 1),
                        parsable.substring(firstDirectionIndex + 1)
                     )
                  }
               }
            }
         }

         // If there is one white space character split on that
         val parts = parsable.split(" ")
         if (parts.size == 2) {
            return parts.map { coordinate -> coordinate.trim() }
         }

         return coordinates
      }

      fun from(latitude: String, longitude: String): DMS? {
         val dmsLatitude = DMSLocation.parse(latitude, CoordinateType.LATITUDE)
         val dmsLongitude = DMSLocation.parse(longitude, CoordinateType.LONGITUDE)

         return if (dmsLatitude != null && dmsLongitude != null) {
            DMS(dmsLatitude, dmsLongitude)
         } else null
      }
   }
}
