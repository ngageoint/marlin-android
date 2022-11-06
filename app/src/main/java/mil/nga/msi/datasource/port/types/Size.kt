package mil.nga.msi.datasource.port.types

enum class Size: EnumerationType {
   V {
      override val title = "Very Small"
   },
   S {
      override val title = "Small"
   },
   M {
      override val title = "Medium"
   },
   L {
      override val title = "Large"
   },
   UNKNOWN {
      override val title = "Unknown"
   };

   companion object {
      fun fromValue(value: String?): Size {
         return  try {
            valueOf(value!!)
         } catch (e: Exception) {
            UNKNOWN
         }
      }
   }
}