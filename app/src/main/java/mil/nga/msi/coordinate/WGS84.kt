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
         } else null
      }
   }

}