package mil.nga.msi.ui.map.search

import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.network.nominatim.NominatimSearchService
import mil.nga.msi.repository.geocoder.GeocoderState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NominatimSearchProvider @Inject constructor(
   private val nominatimSearchService: NominatimSearchService
) {
   suspend fun search(
      text: String
   ): List<GeocoderState> {
      val response = nominatimSearchService.search(text)

      return response.body()?.map {
         GeocoderState(
            name = it.displayName,
            location = LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.lon.toDoubleOrNull() ?: 0.0)
         )
      } ?: emptyList()
   }
}