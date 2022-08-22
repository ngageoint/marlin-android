package mil.nga.msi.datasource.port.types

enum class HarborType(val value: String? = null, val title: String? = null) {
   COASTAL_BREAKWATER("CB", "Coastal Breakwater"),
   COASTAL_NATURAL("CN", "Coastal Natural"),
   COASTAL_TIDE_GATE("CT", "Coastal Tide Gate"),
   LAKE_OR_CANAL("LC", "Lake or Canal"),
   OPEN_ROADSTEAD("OR", "Open Roadstead"),
   RIVER_BASIN("RB", "River Basin"),
   RIVER_NATURAL("RN", "River Natural"),
   RIVER_TIDE_GATE("RT", "River Tide Gate"),
   TYPHOON_HARBOR("TH", "Typhoon Harbor"),
   UNKNOWN("Unknown");

   companion object {
      fun fromValue(value: String?): HarborType {
         return values().find {
            value == it.name
         } ?: UNKNOWN
      }
   }
}