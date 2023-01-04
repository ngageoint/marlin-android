package mil.nga.msi.datasource.port.types

enum class RepairCode: EnumerationType {
   A {
      override val title = "Major"
   },
   B {
      override val title = "Moderate"
   },
   C {
      override val title = "Limited"
   },
   D {
      override val title = "Emergency Only"
   },
   N {
      override val title = "None"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): RepairCode {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}