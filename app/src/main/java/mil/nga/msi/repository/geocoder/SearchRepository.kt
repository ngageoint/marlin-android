package mil.nga.msi.repository.geocoder

import com.google.android.gms.maps.model.LatLng
import mil.nga.gars.GARS
import mil.nga.mgrs.MGRS
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.coordinate.WGS84
import mil.nga.msi.datasource.map.GoogleRemoteDataSource
import mil.nga.msi.datasource.map.NominatimRemoteDataSource
import mil.nga.msi.datasource.map.SearchResult
import mil.nga.msi.geocoder.Place
import mil.nga.msi.ui.map.TileProviderType
import mil.nga.msi.ui.map.search.SearchProvider
import javax.inject.Inject

class SearchRepository @Inject constructor(
   private val googleDataSource: GoogleRemoteDataSource,
   private val nominatimDataSource: NominatimRemoteDataSource
) {
   suspend fun search(text: String, searchProvider: SearchProvider): SearchResult {
      val dms = DMS.from(text)
      val latLng = WGS84.from(text)
      return if (dms != null) {
         val place = Place(
            name = dms.format(),
            location = dms.toLatLng()
         )
         SearchResult.Success(listOf(place))
      } else if (latLng != null) {
         val place = Place(
            name = text,
            location = latLng
         )
         SearchResult.Success(listOf(place))
      } else if (MGRS.isMGRS(text)) {
         val point = MGRS.parse(text).toPoint()
         val place = Place(
            name = TileProviderType.MGRS.name,
            location = LatLng(point.latitude, point.longitude)
         )
         SearchResult.Success(listOf(place))
      } else if (GARS.isGARS(text)) {
         val point = GARS.parse(text).toPoint()
         val place = Place(
            name = TileProviderType.GARS.name,
            location = LatLng(point.latitude, point.longitude)
         )
         SearchResult.Success(listOf(place))
      } else {
         fetchPlaces(text, searchProvider)
      }
   }

   private suspend fun fetchPlaces(
      text: String,
      searchProvider: SearchProvider
   ): SearchResult {
      return when (searchProvider) {
         SearchProvider.GOOGLE -> {
            googleDataSource.search(text)
         }
         SearchProvider.NOMINATIM -> {
            nominatimDataSource.search(text)
         }
      }
   }
}