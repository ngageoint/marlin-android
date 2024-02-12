package mil.nga.msi.ui.map.search

import mil.nga.msi.repository.geocoder.GeocoderState

class NominatimSearchProvider {
   companion object {
      fun search(
         text: String,
         result: (List<GeocoderState>) -> Unit
      ){
         throw RuntimeException("Not implemented")
      }
   }
}