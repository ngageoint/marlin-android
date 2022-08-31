package mil.nga.msi.datasource

import androidx.room.TypeConverter

class StringListTypeConverter {
   @TypeConverter
   fun fromTimestamp(value: String?): List<String>? {
      return value?.split(",")
   }

   @TypeConverter
   fun listToString(list: List<String>?): String? {
      return list?.joinToString(",")
   }
}