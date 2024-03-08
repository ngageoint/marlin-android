package mil.nga.msi.datasource.map

import android.location.Geocoder
import mil.nga.msi.geocoder.getFromLocationName
import javax.inject.Inject

class GoogleRemoteDataSource @Inject constructor(
   private val geocoder: Geocoder
) {
   suspend fun search(text: String): SearchResult {
      return try {
         val places = geocoder.getFromLocationName(text)
         SearchResult.Success(places)
      } catch (e: Exception) {
         return SearchResult.Error("Error searching google, please try again later.")
      }
   }
}