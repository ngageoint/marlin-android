package mil.nga.msi.datasource.port.types

enum class UnderkeelClearance(val value: String? = null, val title: String? = null) {
   STATIC("S", "Static"),
   DYNAIMC("D", "Dynamic"),
   NONE("N", "None"),
   UNKNOWN("Unknown");

   companion object {
      fun fromValue(value: String?): UnderkeelClearance {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}