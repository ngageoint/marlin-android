package mil.nga.msi.geocoder

import android.location.Geocoder
import android.os.Build
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Geocoder.getFromLocationName(text: String): List<Place> = suspendCoroutine { continuation ->
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      try {
         getFromLocationName(text, 10) { addresses ->
            val places = addresses.map { Place.fromAddress(it) }
            continuation.resumeWith(Result.success(places))
         }
      } catch(e: Exception) { continuation.resumeWithException(e) }
   } else {
      try {
         @Suppress("DEPRECATION")
         val places = getFromLocationName(text, 10)?.map {
            Place.fromAddress(it)
         } ?: emptyList()
         continuation.resumeWith(Result.success(places))
      } catch(e: Exception) { continuation.resumeWithException(e) }
   }
}