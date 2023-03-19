package mil.nga.msi.repository.geocoder

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import mil.nga.gars.GARS
import mil.nga.mgrs.MGRS
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.coordinate.WGS84
import mil.nga.msi.ui.map.TileProviderType
import javax.inject.Inject

data class GeocoderState(
   val name: String,
   val address: String? = null,
   val location: LatLng
)

class GeocoderRemoteDataSource @Inject constructor(
   private val geocoder: Geocoder
) {
   suspend fun geocode(text: String): List<GeocoderState> {
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
            fetchAddresses(text)
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

   private suspend fun fetchAddresses(text: String): List<GeocoderState> {
      val results = geocoder.getFromLocationName(text, 10) ?: emptyList()

      return results.mapNotNull {
         if (it.featureName != null) {
            it
         } else null
      }.map {
         GeocoderState(
            name = it.featureName,
            address = it.getAddressLine(0)?.toString(),
            location = LatLng(it.latitude, it.longitude)
         )
      }
   }
}