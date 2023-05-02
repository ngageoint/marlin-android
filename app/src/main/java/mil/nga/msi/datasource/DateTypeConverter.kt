package mil.nga.msi.datasource

import androidx.room.TypeConverter
import java.time.Instant
import java.util.*

class DateTypeConverter {

   @TypeConverter
   fun fromTimestamp(value: Long?): Date? {
      return value?.let { Date(it) }
   }

   @TypeConverter
   fun dateToTimestamp(date: Date?): Long? {
      return date?.time
   }

   @TypeConverter
   fun instantFromTimestamp(x: Long?): Instant? {
      return x?.let { Instant.ofEpochMilli(it) }
   }

   @TypeConverter
   fun timestampFromInstant(x: Instant?): Long? {
      return x?.toEpochMilli()
   }
}