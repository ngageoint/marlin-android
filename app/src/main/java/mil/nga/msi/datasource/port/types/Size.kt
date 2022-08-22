package mil.nga.msi.datasource.port.types

enum class Size(val value: String? = null, val title: String? = null) {
   VERY_SMALL("V", "Very Small"),
   SMALL("S", "Small"),
   MEDIUM("M", "Medium"),
   LARGE("L", "Large"),
   UNKNOWN;

   companion object {
      fun fromValue(value: String?): Size {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}