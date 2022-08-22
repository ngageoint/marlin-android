package mil.nga.msi.datasource.port.types

enum class HarborUse(val value: String? = null, val title: String? = null) {
   FISH("FISH", "Fishing"),
   MILITARY("MIL", "Military"),
   CARGO("CARGO", "Cargo"),
   FERRY("FERRY", "Ferry"),
   UNKNOWN("Unknown");

   companion object {
      fun fromValue(value: String?): HarborUse {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}