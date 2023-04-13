package mil.nga.msi.geocoder

import android.location.Geocoder
import android.os.Build
import mil.nga.msi.repository.geocoder.GeocoderState

fun Geocoder.getFromLocationName(
   text: String,
   result: (List<GeocoderState>) -> Unit
) {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      getFromLocationName(text, 10) { addresses ->
         result(addresses.map { GeocoderState.fromAddress(it) })
      }
   } else {
      try {
         @Suppress("DEPRECATION")
         getFromLocationName(text, 10)?.map {
            GeocoderState.fromAddress(it)
         }?.let { result(it) }
      } catch(e: Exception) { result(emptyList()) }
   }
}