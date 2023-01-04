package mil.nga.msi.datasource.port.types

enum class Decision: EnumerationType {
   Y {
      override val title = "Yes"
   },
   N {
      override val title = "No"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): Decision {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}