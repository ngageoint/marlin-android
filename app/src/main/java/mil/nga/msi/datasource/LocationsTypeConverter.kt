package mil.nga.msi.datasource

import androidx.room.TypeConverter
import com.google.gson.Gson

class LocationsTypeConverter {
   @TypeConverter
   fun fromTimestamp(value: String?): Position? {
      return value?.let { Gson().fromJson(it, Position::class.java) }
   }

   @TypeConverter
   fun locationToJson(position: Position?): String? {
      return position?.let { Gson().toJson(it) }
   }
}