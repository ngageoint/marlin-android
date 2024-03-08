package mil.nga.msi.ui.map.search

enum class SearchType(val title: String, val value: Int) {
   NATIVE("Google Maps", 0),
   NOMINATIM("Nominatim", 1);

   companion object {
      fun fromValue(value: Int?): SearchType {
         return SearchType.entries.find { it.value == value } ?: NATIVE
      }
   }
}