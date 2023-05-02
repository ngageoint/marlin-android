package mil.nga.msi.datasource

import androidx.room.TypeConverter

class StringListTypeConverter {
   @TypeConverter
   fun fromTimestamp(value: String?): List<String> {
      return if (value.isNullOrEmpty()) {
         emptyList()
      } else {
         value.split(",")
      }
   }

   @TypeConverter
   fun listToString(list: List<String>?): String? {
      return if (list?.isEmpty() == true) {
         null
      } else {
         list?.joinToString(",")
      }
   }
}