package mil.nga.msi.datasource.port.types

enum class Decision(val value: String? = null, val title: String? = null) {
   YES("Y", "Yes"),
   NO("N", "No"),
   UNKNOWN("Unknown");

   companion object {
      fun fromValue(value: String?): Decision {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}