package mil.nga.msi.datasource.port.types

enum class HarborUse: EnumerationType {
   FISH {
      override val title = "Fishing"
   },
   MIL {
      override val title = "Military"
   },
   CARGO {
      override val title = "Cargo"
   },
   FERRY {
      override val title = "Ferry"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): HarborUse {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}