package mil.nga.msi.datasource.map

import mil.nga.msi.geocoder.Place

sealed class SearchResult {
   class Success(val places: List<Place>): SearchResult()
   class Error(val message: String): SearchResult()
}