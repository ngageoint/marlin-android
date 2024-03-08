package mil.nga.msi.datasource.map

import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.geocoder.Place
import mil.nga.msi.network.nominatim.NominatimSearchService
import javax.inject.Inject

class NominatimRemoteDataSource @Inject constructor(
   private val service: NominatimSearchService
) {
   suspend fun search(
      text: String
   ): SearchResult {
      return try {
         val response = service.search(text)
         if (response.isSuccessful) {
            val places = response.body()?.map {
               Place(
                  name = it.displayName,
                  location = LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.lon.toDoubleOrNull() ?: 0.0)
               )
            } ?: emptyList()
            SearchResult.Success(places)
         } else {
            SearchResult.Error("Error searching nominatim, please try again later.")
         }
      } catch(e: Exception) {
         SearchResult.Error("Error searching nominatim, please try again later.")
      }
   }
}