package mil.nga.msi.ui.map.search

enum class SearchProvider(val title: String, val value: Int) {
   GOOGLE("Google Maps", 0),
   NOMINATIM("Nominatim", 1);

   companion object {
      fun fromValue(value: Int?): SearchProvider {
         return SearchProvider.entries.find { it.value == value } ?: GOOGLE
      }
   }
}