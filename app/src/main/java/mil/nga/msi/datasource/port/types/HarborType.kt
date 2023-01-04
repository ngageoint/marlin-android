package mil.nga.msi.datasource.port.types

enum class HarborType: EnumerationType {
   CB {
      override val title = "Coastal Breakwater"
   },
   CN {
      override val title = "Coastal Natural"
   },
   CT {
      override val title = "Coastal Tide Gate"
   },
   LC {
      override val title = "Lake or Canal"
   },
   OR {
      override val title = "Open Roadstead"
   },
   RB {
      override val title = "River Basin"
   },
   RN {
      override val title = "River Natural"
   },
   RT {
      override val title = "River Tide Gate"
   },
   TH {
      override val title = "Typhoon Harbor"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): HarborType {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}