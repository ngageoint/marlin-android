package mil.nga.msi.coordinate

import com.google.android.gms.maps.model.LatLng

class WGS84 {

   companion object {
      fun from(text: String): LatLng? {
         val coordinates = text.split(",")
         val latitude = coordinates.getOrNull(0)?.trim()?.toDoubleOrNull()
         val longitude = coordinates.getOrNull(1)?.trim()?.toDoubleOrNull()

         return if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
         } else parse(text)
      }

      private fun parse(text: String): LatLng? {
         var foundLatitude = false
         var foundLongitude = false

         val regex = Regex("(?<latdeg>-?[0-9]*\\.?\\d+)[\\s°-]*(?<latminutes>\\d{1,2}\\.?\\d+)?[\\s\\`'-]*(?<latseconds>\\d{1,2}\\.?\\d+)?[\\s\\\" ]?(?<latdirection>([NOEWS])?)[\\s,]*(?<londeg>-?[0-9]*\\.?\\d+)[\\s°-]*(?<lonminutes>\\d{1,2}\\.?\\d+)?[\\s\\`'-]*(?<lonseconds>\\d{1,2}\\.?\\d+)?[\\s\\\" ]*(?<londirection>([NOEWS])?)")
         var latitudeDegrees = 0.0
         var latitudeMultiplier = 1.0
         var longitudeDegrees = 0.0
         var longitudeMultiplier = 1.0

         regex.findAll(text).forEach { match ->
            val groups =  match.groups as? MatchNamedGroupCollection
            if (groups != null) {
               arrayOf("latdeg", "latminutes", "latseconds", "latdirection", "londeg", "lonminutes", "lonseconds", "londirection").forEach { name ->
                  val component = groups[name]
                  if (component != null) {
                     when (name) {
                        "latdirection" -> {
                           latitudeMultiplier =
                              if ("NEO".contains(text.substring(component.range))) 1.0 else -1.0
                        }

                        "latdeg" -> {
                           foundLatitude = true
                           latitudeDegrees += text.substring(component.range).toDoubleOrNull()
                              ?: 0.0
                        }

                        "latminutes" -> {
                           latitudeDegrees += (text.substring(component.range).toDoubleOrNull()
                              ?: 0.0) / 60
                        }

                        "latseconds" -> {
                           latitudeDegrees += (text.substring(component.range).toDoubleOrNull()
                              ?: 0.0) / 3600
                        }

                        "londirection" -> {
                           longitudeMultiplier =
                              if ("NEO".contains(text.substring(component.range))) 1.0 else -1.0
                        }

                        "londeg" -> {
                           foundLongitude = true
                           longitudeDegrees += text.substring(component.range).toDoubleOrNull()
                              ?: 0.0
                        }

                        "lonminutes" -> {
                           longitudeDegrees += (text.substring(component.range).toDoubleOrNull()
                              ?: 0.0) / 60
                        }

                        "lonseconds" -> {
                           longitudeDegrees += (text.substring(component.range).toDoubleOrNull()
                              ?: 0.0) / 3600
                        }
                     }
                  }
               }
            }
         }

         return if (foundLatitude && foundLongitude) {
            LatLng(
               latitudeMultiplier * latitudeDegrees,
               longitudeMultiplier * longitudeDegrees
            )
         } else null
      }
   }
}