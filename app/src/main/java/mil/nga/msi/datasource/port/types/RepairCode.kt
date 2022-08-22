package mil.nga.msi.datasource.port.types

enum class RepairCode(val value: String? = null, val title: String? = null) {
   MAJOR("A", "Major"),
   MODERATE("B", "Moderate"),
   LIMITED("C", "Limited"),
   EMERGENCY_ONLY("D", "Emergency Only"),
   NONE("N", "None"),
   UNKNOWN("Unknown");

   companion object {
      fun fromCode(code: String?): RepairCode {
         return values().find {
            code == it.name
         } ?: UNKNOWN
      }
   }
}