package mil.nga.msi.repository.geocoder

import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import mil.nga.gars.GARS
import mil.nga.mgrs.MGRS
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.coordinate.WGS84
import mil.nga.msi.geocoder.getFromLocationName
import mil.nga.msi.ui.map.TileProviderType
import mil.nga.msi.ui.map.search.NominatimSearchProvider
import mil.nga.msi.ui.map.search.SearchType
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

data class GeocoderState(
   val name: String,
   val address: String? = null,
   val location: LatLng
) {
   companion object {
      fun fromAddress(address: Address): GeocoderState {
         return GeocoderState(
            name = address.featureName,
            address = address.getAddressLine(0)?.toString(),
            location = LatLng(address.latitude, address.longitude)
         )
      }
   }
}

class GeocoderRemoteDataSource @Inject constructor(
   private val geocoder: Geocoder,
   private val nominatimSearchProvider: NominatimSearchProvider
) {
   suspend fun geocode(text: String, searchType: SearchType): List<GeocoderState> {
      val dms = DMS.from(text)
      return if (dms != null) {
         val state = GeocoderState(
            name = dms.format(),
            location = dms.toLatLng()
         )
         listOf(state)
      } else if (MGRS.isMGRS(text)) {
         val point = MGRS.parse(text).toPoint()
         val state = GeocoderState(
            name = TileProviderType.MGRS.name,
            location = LatLng(point.latitude, point.longitude)
         )
         listOf(state)
      } else if (GARS.isGARS(text)) {
         val point = GARS.parse(text).toPoint()
         val state = GeocoderState(
            name = TileProviderType.GARS.name,
            location = LatLng(point.latitude, point.longitude)
         )
         listOf(state)
      } else {
         val addresses = try {
            fetchAddresses(text, searchType)
         } catch (e: Exception) { emptyList() }

         val location = WGS84.from(text)?.let { latLng ->
            listOf(
               GeocoderState(
                  name = text,
                  location = latLng
               )
            )
         } ?: emptyList()

         location + addresses
      }
   }

   private suspend fun fetchAddresses(
      text: String,
      searchType: SearchType
   ): List<GeocoderState> {
      return when (searchType) {
         SearchType.NATIVE -> {
            suspendCoroutine { continuation ->
               geocoder.getFromLocationName(text) {
                  continuation.resumeWith(Result.success(it))
               }
            }
         }
         SearchType.NOMINATIM -> {
            nominatimSearchProvider.search(text)
         }
      }
   }
}