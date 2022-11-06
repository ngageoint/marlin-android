package mil.nga.msi.datasource.port.types

enum class UnderkeelClearance: EnumerationType {
   S {
      override val title = "Static"
   },
   D {
      override val title = "Dynamic"
   },
   N {
      override val title = "None"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): UnderkeelClearance {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}