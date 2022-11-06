package mil.nga.msi.datasource.port.types

enum class Shelter: EnumerationType {
   E {
      override val title = "Excellent"
   },
   G {
      override val title = "Good"
   },
   F {
      override val title = "Fair"
   },
   P {
      override val title = "Poor"
   },
   N {
      override val title = "None"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): Shelter {
         return try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}