package mil.nga.msi.datasource.port.types

enum class Shelter(val value: String? = null, val title: String) {
   EXCELLENT("E", "Excellent"),
   GOOD("G", "Good"),
   FAIR("F", "Fair"),
   POOR("P", "Poor"),
   NONE("N", "None"),
   UNKNOWN(title = "Unknown");

   companion object {
      fun fromValue(value: String?): Shelter {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}